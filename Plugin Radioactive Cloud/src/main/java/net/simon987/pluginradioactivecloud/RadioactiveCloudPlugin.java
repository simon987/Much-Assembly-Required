package net.simon987.pluginradioactivecloud;

import net.simon987.server.GameServer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;

public class RadioactiveCloudPlugin extends ServerPlugin {

    @Override
    public void init(GameServer gameServer) {

        LogManager.LOGGER.info("(Radioactive cloud Plugin) Initialised Radioactive cloud plugin.");
    }
}