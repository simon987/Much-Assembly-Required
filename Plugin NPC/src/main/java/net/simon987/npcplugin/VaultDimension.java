package net.simon987.npcplugin;

import net.simon987.npcplugin.world.TileVaultFloor;
import net.simon987.server.GameServer;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.objects.Direction;
import net.simon987.server.game.world.Location;
import net.simon987.server.game.world.World;
import net.simon987.server.logging.LogManager;
import org.bson.types.ObjectId;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class VaultDimension {

    /**
     * Name of the dimension
     */
    private String name;

    private World homeWorld;

    private int homeX;
    private int homeY;

    public VaultDimension(VaultDoor vaultDoor) {

        name = "v" + vaultDoor.getObjectId() + "-";

        /*
         * Creates a group of vault worlds and pieces them together with openings.
         * For a set number of passes, a random number of vault worlds are added to each world in the
         * previous 'layer' of worlds in a random direction. Openings are added to allow movement from a
         * layer to the next, meaning that adjacent worlds are not necessarily connected, and one would
         * necessarily need to travel through 5 openings to reach the 6th layer, even when that layer is
         * less than 5 worlds away from the origin/home vault world (the one containing the exit door).
         *
         * 1. Create home world (layer 0)
         * 2. For each world in the current layer, attach a random number of new worlds
         * 3. Repeat the same step for the newly added layer
         * 4. Choose a random world from the last layer and create the vault box there (objective)
         * 5. Create an exit portal in the home world
         *
         * This process is done in 2 passes, in the first pass, worlds are defined
         * as a set of coordinates + a list of opening directions, then they are actually generated
         */

        ServerConfiguration config = GameServer.INSTANCE.getConfig();

        int minLayerCount = config.getInt("vault_wg_min_layer_count");
        int maxLayerCount = config.getInt("vault_wg_max_layer_count");
        int minAttachedWorld = config.getInt("vault_wg_min_attached_world");
        int maxAttachedWorld = Math.min(config.getInt("vault_wg_max_attached_world"), 4);
        int minElectricBoxCount = config.getInt("vault_wg_min_electric_box_count");
        int maxElectricBoxCount = config.getInt("vault_wg_max_electric_box_count");

        HashMap<Integer, ArrayList<WorldBluePrint>> worldLayers = new HashMap<>();
        VaultWorldGenerator generator = new VaultWorldGenerator();
        Random random = new Random();

        int layerCount = random.nextInt(maxLayerCount - minLayerCount) + minLayerCount;

        //1. Create home world
        WorldBluePrint homeWorldBluePrint = new WorldBluePrint();
        homeWorldBluePrint.coords.x = 0x7FFF;
        homeWorldBluePrint.coords.y = 0x7FFF;
        worldLayers.put(0, new ArrayList<>());
        worldLayers.get(0).add(homeWorldBluePrint);

        //2. For each world in the current layer, attach a random number of new worlds
        for (int i = 1; i <= layerCount; i++) {

            worldLayers.put(i, new ArrayList<>());

            for (WorldBluePrint world : worldLayers.get(i - 1)) {

                int attachedWorlds;
                if (i == 1) {
                    attachedWorlds = 4; // The home world should have 4 attached worlds
                } else {
                    attachedWorlds = random.nextInt(maxAttachedWorld - minAttachedWorld) + minAttachedWorld;
                }

                for (int j = 0; j < attachedWorlds; j++) {

                    int randDirIndex = random.nextInt(4);

                    //Try 4 directions (wrap around 0..3)
                    for (int attemptCount = 0; attemptCount < 4; attemptCount++) {
                        Direction randomDirection = Direction.getDirection(randDirIndex);

                        //Don't attach a world at the same spot twice
                        if (!worldExists(world.coordinatesOf(randomDirection), worldLayers)) {
                            WorldBluePrint attachedWorld = world.attachWorld(randomDirection);
                            worldLayers.get(i).add(attachedWorld);
                        }
                        randDirIndex = (randDirIndex + 1) % 4;
                    }
                }
            }
        }

        ArrayList<World> lastLayerWorlds = new ArrayList<>();

        //Generate worlds
        for (Integer key : worldLayers.keySet()) {

            ArrayList<WorldBluePrint> layer = worldLayers.get(key);

            for (WorldBluePrint bp : layer) {
                World vWorld = generator.generateVaultWorld(bp.coords.x, bp.coords.y, bp.openings, name);
                GameServer.INSTANCE.getGameUniverse().addWorld(vWorld);

                ArrayList<ElectricBox> newBoxes = VaultWorldUtils.generateElectricBoxes(vWorld, minElectricBoxCount,
                        maxElectricBoxCount);
                for (ElectricBox blob : newBoxes) {
                    vWorld.addObject(blob);
                    vWorld.incUpdatable();
                }

                if (key == layerCount) {
                    lastLayerWorlds.add(vWorld);
                }

                if (key == 0) {
                    this.homeWorld = vWorld;
                }
            }
        }

        Point exitCoords = vaultDoor.getAdjacentTile();
        Location exitLocation = new Location(vaultDoor.getWorld().getX(), vaultDoor.getWorld().getY(), vaultDoor
                .getWorld().getDimension(), exitCoords.x, exitCoords.y);


        //4. Choose a random world from the last layer and create the vault box there (objective)
        World objectiveWorld = lastLayerWorlds.get(random.nextInt(lastLayerWorlds.size()));

        Point exitPortalPt = objectiveWorld.getRandomTileWithAdjacent(8, TileVaultFloor.ID);

        if (exitPortalPt != null) {

            VaultExitPortal exitPortal = new VaultExitPortal();
            exitPortal.setDestination(exitLocation);
            exitPortal.setX(exitPortalPt.x);
            exitPortal.setY(exitPortalPt.y);
            exitPortal.setWorld(objectiveWorld);
            exitPortal.setObjectId(new ObjectId());
            objectiveWorld.addObject(exitPortal);

//            LogManager.LOGGER.severe("Objective: " + objectiveWorld.getId());

        } else {
            LogManager.LOGGER.severe("FIXME: Couldn't create exit portal for world " + homeWorld.getId());
        }

        //5. Create an exit portal in the home World
        Point homePortalPt = homeWorld.getRandomTileWithAdjacent(8, TileVaultFloor.ID);
        if (homePortalPt != null) {

            Portal homePortal = new Portal();
            homePortal.setDestination(exitLocation);
            homePortal.setX(homePortalPt.x);
            homePortal.setY(homePortalPt.y);
            homePortal.setWorld(homeWorld);
            homePortal.setObjectId(new ObjectId());
            homeWorld.addObject(homePortal);

            Point entryCoords = homePortal.getAdjacentTile();
            homeX = entryCoords.x;
            homeY = entryCoords.y;

        } else {
            LogManager.LOGGER.severe("FIXME: Couldn't create home exit portal for world " + homeWorld.getId());
        }


    }

    private boolean worldExists(Point coords, HashMap<Integer, ArrayList<WorldBluePrint>> worldLayers) {

        //Auto-generated by IntelliJ Idea
        return worldLayers.values().stream().flatMap(Collection::stream).anyMatch(bp -> bp.coords.equals(coords));
    }

    World getHomeWorld() {
        return homeWorld;
    }

    public int getHomeX() {
        return homeX;
    }

    public int getHomeY() {
        return homeY;
    }
    /**
     * Helper class to plan the layout of a vault dimension
     */
    private class WorldBluePrint {

        ArrayList<Direction> openings = new ArrayList<>(4);

        /**
         * Coordinates of the world
         */
        Point coords = new Point();

        /**
         * Update the blueprint's openings to allow traveling to the newly attached world
         *
         * @param direction direction of the world to attach (relative to this one)
         * @return The blueprint of the attached world
         */
        WorldBluePrint attachWorld(Direction direction) {

            WorldBluePrint attachedWorld = new WorldBluePrint();

            switch (direction) {
                case NORTH:
                    openings.add(Direction.NORTH);
                    attachedWorld.openings.add(Direction.SOUTH);
                    break;
                case EAST:
                    openings.add(Direction.EAST);
                    attachedWorld.openings.add(Direction.WEST);
                    break;
                case SOUTH:
                    openings.add(Direction.SOUTH);
                    attachedWorld.openings.add(Direction.NORTH);
                    break;
                case WEST:
                    openings.add(Direction.WEST);
                    attachedWorld.openings.add(Direction.EAST);
                    break;
            }

            attachedWorld.coords.x = coords.x + direction.dX;
            attachedWorld.coords.y = coords.y + direction.dY;

            return attachedWorld;
        }

        /**
         * Get the coordinates of a world that would be attached to this world
         *
         * @param direction direction of the attached world
         */
        Point coordinatesOf(Direction direction) {

            return new Point(coords.x + direction.dX, coords.y + direction.dY);

        }
    }
}
