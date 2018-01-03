package net.simon987.server.io;

import com.mongodb.DBObject;
import net.simon987.server.assembly.CpuHardware;

public interface CpuHardwareDeserializer {


    CpuHardware deserializeHardware(DBObject hwJson);
}
