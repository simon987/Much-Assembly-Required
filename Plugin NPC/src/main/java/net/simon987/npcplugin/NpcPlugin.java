package net.simon987.npcplugin;

import net.simon987.npcplugin.event.WorldUpdateListener;
import net.simon987.server.game.GameObject;
import net.simon987.server.io.GameObjectDeserializer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;
import org.json.simple.JSONObject;

public class NpcPlugin extends ServerPlugin implements GameObjectDeserializer {


    @Override
    public void init() {

        listeners.add(new WorldUpdateListener());

        LogManager.LOGGER.info("Initialised NPC plugin");
    }

    @Override
    public GameObject deserializeObject(JSONObject object) {

        int objType = (int) (long) object.get("type");

        if (objType == HarvesterNPC.ID) {
            return HarvesterNPC.deserialize(object);
        }

        return null;
    }
}
