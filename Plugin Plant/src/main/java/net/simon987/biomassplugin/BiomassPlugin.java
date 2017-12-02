package net.simon987.biomassplugin;

import net.simon987.biomassplugin.event.WorldCreationListener;
import net.simon987.biomassplugin.event.WorldUpdateListener;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.GameObject;
import net.simon987.server.io.GameObjectDeserializer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;
import org.json.simple.JSONObject;

public class BiomassPlugin extends ServerPlugin implements GameObjectDeserializer {


    @Override
    public void init(ServerConfiguration config) {
        listeners.add(new WorldCreationListener());
        listeners.add(new WorldUpdateListener(config));

        LogManager.LOGGER.info("Initialised Biomass plugin");
    }

    @Override
    public GameObject deserializeObject(JSONObject object) {

        int objType = (int) (long) object.get("type");

        if (objType == BiomassBlob.ID) {

            return BiomassBlob.deserialize(object);
        }

        return null;
    }
}
