package net.simon987.mischwplugin;

import net.simon987.mischwplugin.event.CpuInitialisationListener;
import net.simon987.server.GameServer;
import net.simon987.server.game.objects.GameRegistry;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;

/**
 * Plugin that adds miscellaneous hardware to the game
 */
public class MiscHWPlugin extends ServerPlugin {


    @Override
    public void init(GameServer gameServer) {
        listeners.add(new CpuInitialisationListener());

        GameRegistry registry = gameServer.getRegistry();

        registry.registerHardware(RandomNumberGenerator.class);
        registry.registerHardware(Clock.class);

        LogManager.LOGGER.info("(Mist HW Plugin) Initialised Misc Hardware Plugin");
    }
}
