package net.simon987.pluginradioactivecloud;

import com.mongodb.DBObject;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.GameObject;
import net.simon987.server.io.GameObjectDeserializer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;

public class RadioactiveCloudPlugin extends ServerPlugin implements GameObjectDeserializer {

    @Override
    public void init(ServerConfiguration config) {

        LogManager.LOGGER.info("Initialised Radioactive cloud plugin.");
    }

    @Override
    public GameObject deserializeObject(DBObject object) {

        return null;
    }
}
