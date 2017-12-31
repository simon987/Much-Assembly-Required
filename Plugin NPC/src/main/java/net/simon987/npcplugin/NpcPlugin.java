package net.simon987.npcplugin;

import net.simon987.npcplugin.event.CpuInitialisationListener;
import net.simon987.npcplugin.event.WorldCreationListener;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.game.GameObject;
import net.simon987.server.io.CpuHardwareDeserializer;
import net.simon987.server.io.GameObjectDeserializer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class NpcPlugin extends ServerPlugin implements GameObjectDeserializer, CpuHardwareDeserializer {

    /**
     * Radio tower cache
     */
    private static ArrayList<RadioTower> radioTowers;

    @Override
    public void init(ServerConfiguration configuration) {

        listeners.add(new WorldCreationListener());
        listeners.add(new CpuInitialisationListener());

        radioTowers = new ArrayList<>(32);

        LogManager.LOGGER.info("Initialised NPC plugin");
    }

    @Override
    public GameObject deserializeObject(JSONObject json) {

        int objType = (int) (long) json.get("t");

        if (objType == HarvesterNPC.ID) {
            return HarvesterNPC.deserialize(json);
        } else if (objType == Factory.ID) {
            return Factory.deserialise(json);
        } else if (objType == RadioTower.ID) {
            return RadioTower.deserialize(json);
        }

        return null;
    }

    @Override
    public CpuHardware deserializeHardware(JSONObject hwJson) {
        int hwid = (int) (long) hwJson.get("hwid");

        switch (hwid) {
            case RadioReceiverHardware.HWID:
                return RadioReceiverHardware.deserialize(hwJson);
        }

        return null;
    }

    public static ArrayList<RadioTower> getRadioTowers() {
        return radioTowers;
    }
}
