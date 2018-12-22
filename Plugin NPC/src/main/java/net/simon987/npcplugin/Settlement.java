package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.world.TilePlain;
import net.simon987.server.game.world.World;
import net.simon987.server.game.world.WorldGenerationException;
import net.simon987.server.io.MongoSerializable;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Settlement implements MongoSerializable {

    private Factory factory = null;
    private RadioTower radioTower = null;
    private VaultDoor vaultDoor = null;
    private World world;
    private DifficultyLevel difficultyLevel;

    private List<NonPlayerCharacter> npcs = new ArrayList<>();

    private char[] password;

    public Settlement(Document document) {

        world = GameServer.INSTANCE.getGameUniverse().getWorld(document.getString("world"), false);
        ObjectId radioTowerId = document.getObjectId("radio_tower");
        if (radioTowerId != null) {
            radioTower = (RadioTower) GameServer.INSTANCE.getGameUniverse().getObject(radioTowerId);
        }
        ObjectId vaultDoorId = document.getObjectId("vault_door");
        if (vaultDoorId != null) {
            vaultDoor = (VaultDoor) GameServer.INSTANCE.getGameUniverse().getObject(vaultDoorId);
        }
        ObjectId factoryId = document.getObjectId("factory");
        factory = (Factory) GameServer.INSTANCE.getGameUniverse().getObject(factoryId);

        difficultyLevel = DifficultyLevel.values()[document.getInteger("difficulty_level")];

        Object[] npcArray = ((ArrayList) document.get("npcs")).toArray();
        for (Object id : npcArray) {

            NonPlayerCharacter npc = (NonPlayerCharacter) GameServer.INSTANCE.getGameUniverse().getObject((ObjectId) id);

            if (npc != null) {
                addNpc(npc);
            }
        }

        password = document.getString("password").toCharArray();
    }

    public Settlement(World world) throws WorldGenerationException {

        this.world = world;
        this.difficultyLevel = DifficultyLevel.NORMAL; //TODO randomize ?
        this.password = "12345678".toCharArray();

        outerLoopFactory:
        for (int x = 2; x < 12; x++) {
            for (int y = 2; y < 12; y++) {

                if ((!world.isTileBlocked(x, y) && !world.isTileBlocked(x + 1, y) &&
                        !world.isTileBlocked(x, y + 1) && !world.isTileBlocked(x + 1, y + 1))) {

                    Factory factory = new Factory();

                    factory.setWorld(world);
                    factory.setObjectId(new ObjectId());
                    factory.setX(x);
                    factory.setY(y);

                    if (factory.getAdjacentTile() == null) {
                        //Factory has no non-blocked adjacent tiles
                        continue;
                    }

                    world.addObject(factory);
                    world.incUpdatable();
                    this.factory = factory;

                    break outerLoopFactory;
                }
            }
        }
        if (factory == null) {
            throw new WorldGenerationException("Could not place Factory");
        }

        //Also spawn a radio tower in the same World
        Point p = world.getRandomTileWithAdjacent(8, TilePlain.ID);
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
            radioTower.setObjectId(new ObjectId());
            radioTower.setX(p.x);
            radioTower.setY(p.y);

            if (radioTower.getAdjacentTile() != null) {
                //Radio Tower has adjacent tiles
                world.addObject(radioTower);
                world.incUpdatable(); //In case the Factory couldn't be spawned.

                this.radioTower = radioTower;
            }
        }

        //Also spawn a Vault in the same World
        p = world.getRandomPassableTile();
        if (p != null) {

            VaultDoor vaultDoor = new VaultDoor();
            vaultDoor.setWorld(world);

            int counter = 700;
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

            vaultDoor.setObjectId(new ObjectId());
            world.addObject(vaultDoor);
            world.incUpdatable(); //In case the Factory & Radio Tower couldn't be spawned.
            vaultDoor.setWorld(world);

            vaultDoor.initialize();
            this.vaultDoor = vaultDoor;
        }
    }

    public void addNpc(NonPlayerCharacter npc) {
        npcs.add(npc);
        npc.setSettlement(this);
    }

    @Override
    public Document mongoSerialise() {
        Document document = new Document();

        document.put("world", world.getId());
        if (radioTower != null) {
            document.put("radio_tower", radioTower.getObjectId());
        }
        if (vaultDoor != null) {
            document.put("vault_door", vaultDoor.getObjectId());
        }
        document.put("factory", factory.getObjectId());
        document.put("difficulty_level", difficultyLevel.ordinal());
        document.put("password", String.valueOf(password));


        List<ObjectId> npcArray = new ArrayList<>(npcs.size());
        for (NonPlayerCharacter npc : npcs) {
            npcArray.add(npc.getObjectId());
        }
        document.put("npcs", npcArray);

        return document;
    }

    public enum DifficultyLevel {
        NORMAL(0);

        public int cypherId;

        DifficultyLevel(int cypherId) {
            this.cypherId = cypherId;
        }
    }

    public Factory getFactory() {
        return factory;
    }

    public RadioTower getRadioTower() {
        return radioTower;
    }

    public VaultDoor getVaultDoor() {
        return vaultDoor;
    }

    public World getWorld() {
        return world;
    }

    public List<NonPlayerCharacter> getNpcs() {
        return npcs;
    }

    public char[] getPassword() {
        return password;
    }
}
