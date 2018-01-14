package net.simon987.npcplugin;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;

public class VaultPlugin extends ServerPlugin {


    @Override
    public void init(ServerConfiguration config) {


        LogManager.LOGGER.info("Initialised Vault plugin");
    }
}
