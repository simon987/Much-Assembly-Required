package net.simon987.pluginradioactivecloud;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.GameObject;
import net.simon987.server.io.GameObjectDeserializer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;
import org.bson.Document;

public class RadioactiveCloudPlugin extends ServerPlugin implements GameObjectDeserializer {

    @Override
    public void init(ServerConfiguration config) {

        LogManager.LOGGER.info("Initialised Radioactive cloud plugin.");
    }

    @Override
    public GameObject deserializeObject(Document object) {

        return null;
    }
}
