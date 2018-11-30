package net.simon987.pluginradioactivecloud;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.objects.GameRegistry;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;

public class RadioactiveCloudPlugin extends ServerPlugin {

    @Override
    public void init(ServerConfiguration config, GameRegistry registry) {

        LogManager.LOGGER.info("(Radioactive cloud Plugin) Initialised Radioactive cloud plugin.");
    }
}