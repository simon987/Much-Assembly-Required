package net.simon987.npcplugin.event;

import net.simon987.npcplugin.Factory;
import net.simon987.npcplugin.NpcPlugin;
import net.simon987.npcplugin.RadioTower;
import net.simon987.npcplugin.VaultDoor;
import net.simon987.server.GameServer;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.event.WorldGenerationEvent;
import net.simon987.server.game.World;
import net.simon987.server.logging.LogManager;

import java.awt.*;
import java.util.Random;

public class WorldCreationListener implements GameEventListener {

    /**
     * Spawn rate. Higher = rarer: A factory will be spawn about every FACTORY_SPAWN_RATE generated Worlds
     */
    private static final int FACTORY_SPAWN_RATE = 35;

    private Random random = new Random();

    @Override
    public Class getListenedEventType() {
        return WorldGenerationEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        if (random.nextInt(FACTORY_SPAWN_RATE) == 0) {

            World world = ((WorldGenerationEvent) event).getWorld();

            outerLoopFactory:
            for (int x = 2; x < 12; x++) {
                for (int y = 2; y < 12; y++) {

                    if ((!world.isTileBlocked(x, y) && !world.isTileBlocked(x + 1, y) &&
                            !world.isTileBlocked(x, y + 1) && !world.isTileBlocked(x + 1, y + 1))) {

                        Factory factory = new Factory();

                        factory.setWorld(world);
                        factory.setObjectId(GameServer.INSTANCE.getGameUniverse().getNextObjectId());
                        factory.setX(x);
                        factory.setY(y);

                        if (factory.getAdjacentTile() == null) {
                            //Factory has no non-blocked adjacent tiles
                            continue;
                        }

                        world.addObject(factory);
                        world.incUpdatable();

                        LogManager.LOGGER.info("Spawned Factory at (" + world.getX() + ", " + world.getY() +
                                ") (" + x + ", " + y + ")");
                        break outerLoopFactory;
                    }
                }
            }

            //Also spawn a radio tower in the same World
            Point p = world.getRandomPassableTile();
            if (p != null) {
                while (p.x == 0 || p.x == world.getWorldSize() - 1 || p.y == world.getWorldSize() - 1 || p.y == 0) {
                    p = world.getRandomPassableTile();

                    if (p == null) {
                        //World is full
                        return;
                    }
                }

                RadioTower radioTower = new RadioTower();

                radioTower.setWorld(world);
                radioTower.setObjectId(GameServer.INSTANCE.getGameUniverse().getNextObjectId());
                radioTower.setX(p.x);
                radioTower.setY(p.y);

                if (radioTower.getAdjacentTile() != null) {
                    //Radio Tower has adjacent tiles
                    world.addObject(radioTower);
                    world.incUpdatable(); //In case the Factory couldn't be spawned.

                    NpcPlugin.getRadioTowers().add(radioTower);

                    LogManager.LOGGER.info("Spawned RadioTower at (" + world.getX() + ", " + world.getY() +
                            ") (" + p.x + ", " + p.y + ")");
                }
            }

            //Also spawn a Vault in the same World
            p = world.getRandomPassableTile();
            if (p != null) {

                VaultDoor vaultDoor = new VaultDoor(0); //todo cypherId ?

                vaultDoor.setWorld(world);
                vaultDoor.setObjectId(GameServer.INSTANCE.getGameUniverse().getNextObjectId());
                vaultDoor.setX(p.x);
                vaultDoor.setY(p.y);

                int counter = 300;
                while (p.x == 0 || p.x == world.getWorldSize() - 1 || p.y == world.getWorldSize() - 1 || p.y == 0
                        || vaultDoor.getAdjacentTileCount(true) < 8) {
                    p = world.getRandomPassableTile();

                    if (p == null) {
                        //World is full
                        return;
                    }

                    vaultDoor.setX(p.x);
                    vaultDoor.setY(p.y);

                    counter--;

                    if (counter <= 0) {
                        //Reached maximum amount of retries
                        return;
                    }
                }

                world.addObject(vaultDoor);
                world.incUpdatable(); //In case the Factory & Radio Tower couldn't be spawned.

                LogManager.LOGGER.info("Spawned Vault Door at (" + world.getX() + ", " + world.getY() +
                        ") (" + p.x + ", " + p.y + ")");
            }
        }
    }
}
