package net.simon987.npcplugin;

import com.mongodb.DBObject;
import net.simon987.npcplugin.event.CpuInitialisationListener;
import net.simon987.npcplugin.event.VaultWorldUpdateListener;
import net.simon987.npcplugin.event.WorldCreationListener;
import net.simon987.npcplugin.io.StatsDatabaseManager;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.game.GameObject;
import net.simon987.server.io.CpuHardwareDeserializer;
import net.simon987.server.io.GameObjectDeserializer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;

import java.util.ArrayList;

public class NpcPlugin extends ServerPlugin implements GameObjectDeserializer, CpuHardwareDeserializer {

    /**
     * Radio tower cache
     */
    private static ArrayList<RadioTower> radioTowers;

    private static StatsDatabaseManager statsDbManager;

    @Override
    public void init(ServerConfiguration configuration) {

        listeners.add(new WorldCreationListener());
        listeners.add(new CpuInitialisationListener());
        listeners.add(new VaultWorldUpdateListener(configuration));

        radioTowers = new ArrayList<>(32);

        statsDbManager = new StatsDatabaseManager(configuration);

        LogManager.LOGGER.info("Initialised NPC plugin");
    }

    @Override
    public GameObject deserializeObject(DBObject obj) {

        int objType = (int) obj.get("t");

        if (objType == HarvesterNPC.ID) {
            return HarvesterNPC.deserialize(obj);
        } else if (objType == Factory.ID) {
            return Factory.deserialise(obj);
        } else if (objType == RadioTower.ID) {
            return RadioTower.deserialize(obj);
        } else if (objType == VaultDoor.ID) {
            return VaultDoor.deserialize(obj);
        } else if (objType == Obstacle.ID) {
            return Obstacle.deserialize(obj);
        } else if (objType == ElectricBox.ID) {
            return ElectricBox.deserialize(obj);
        } else if (objType == Portal.ID) {
            return Portal.deserialize(obj);
        } else if (objType == VaultExitPortal.ID) {
            return VaultExitPortal.deserialize(obj);
        }

        return null;
    }

    @Override
    public CpuHardware deserializeHardware(DBObject obj) {
        int hwid = (int) obj.get("hwid");

        switch (hwid) {
            case RadioReceiverHardware.HWID:
                return RadioReceiverHardware.deserialize(obj);
        }

        return null;
    }

    public static ArrayList<RadioTower> getRadioTowers() {
        return radioTowers;
    }

    public static StatsDatabaseManager getStatsDbManager() {
        return statsDbManager;
    }
}
