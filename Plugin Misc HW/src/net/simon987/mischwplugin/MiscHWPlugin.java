package net.simon987.mischwplugin;

import net.simon987.mischwplugin.event.CpuInitialisationListener;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.io.CpuHardwareDeserializer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;
import org.json.simple.JSONObject;

public class MiscHWPlugin extends ServerPlugin implements CpuHardwareDeserializer {


    @Override
    public void init() {
        listeners.add(new CpuInitialisationListener());

        LogManager.LOGGER.info("Initialised Misc Hardware Plugin");
    }

    @Override
    public CpuHardware deserializeHardware(JSONObject hwJson) {
        int hwid = (int) (long) hwJson.get("hwid");

        switch (hwid) {
            case RandomNumberGenerator.HWID:
                return RandomNumberGenerator.deserialize(hwJson);
        }

        return null;
    }
}
