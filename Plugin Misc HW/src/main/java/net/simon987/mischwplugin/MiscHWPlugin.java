package net.simon987.mischwplugin;

import net.simon987.mischwplugin.event.CpuInitialisationListener;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.io.CpuHardwareDeserializer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;
import org.bson.Document;

/**
 * Plugin that adds miscellaneous hardware to the game
 */
public class MiscHWPlugin extends ServerPlugin implements CpuHardwareDeserializer {


    @Override
    public void init(ServerConfiguration config) {
        listeners.add(new CpuInitialisationListener());

        LogManager.LOGGER.info("Initialised Misc Hardware Plugin");
    }

    @Override
    public CpuHardware deserializeHardware(Document hwJson) {
        int hwid = (int) hwJson.get("hwid");

        switch (hwid) {
            case RandomNumberGenerator.HWID:
                return RandomNumberGenerator.deserialize();
            case Clock.HWID:
                return Clock.deserialize();
        }

        return null;
    }
}
