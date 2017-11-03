package net.simon987.plantplugin;

import net.simon987.plantplugin.event.WorldCreationListener;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.GameObjectDeserializer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;
import org.json.simple.JSONObject;

public class PlantPlugin extends ServerPlugin implements GameObjectDeserializer {



    @Override
    public void init() {
        listeners.add(new WorldCreationListener());
        LogManager.LOGGER.info("Initialised Plant plugin");
    }

    @Override
    public GameObject deserializeObject(JSONObject object) {

        int objType = (int)(long)object.get("type");

        if(objType == Plant.ID) {

            return Plant.deserialize(object);
        }

        return null;
    }
}
