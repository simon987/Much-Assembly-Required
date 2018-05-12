package net.simon987.server.io;

import net.simon987.server.assembly.CpuHardware;
import org.bson.Document;

public interface CpuHardwareDeserializer {


    CpuHardware deserializeHardware(Document hwJson);
}
