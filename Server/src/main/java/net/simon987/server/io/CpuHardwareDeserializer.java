package net.simon987.server.io;

import net.simon987.server.assembly.CpuHardware;
import org.json.simple.JSONObject;

public interface CpuHardwareDeserializer {


    CpuHardware deserializeHardware(JSONObject hwJson);
}
