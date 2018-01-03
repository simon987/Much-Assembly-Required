package net.simon987.mischwplugin;

import com.mongodb.DBObject;
import net.simon987.mischwplugin.event.CpuInitialisationListener;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.io.CpuHardwareDeserializer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;

public class MiscHWPlugin extends ServerPlugin implements CpuHardwareDeserializer {


    @Override
    public void init(ServerConfiguration config) {
        listeners.add(new CpuInitialisationListener());

        LogManager.LOGGER.info("Initialised Misc Hardware Plugin");
    }

    @Override
    public CpuHardware deserializeHardware(DBObject hwJson) {
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
