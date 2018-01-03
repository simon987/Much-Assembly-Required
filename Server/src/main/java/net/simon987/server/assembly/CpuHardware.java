package net.simon987.server.assembly;


import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.io.CpuHardwareDeserializer;
import net.simon987.server.io.MongoSerialisable;
import net.simon987.server.plugin.ServerPlugin;

public abstract class CpuHardware implements MongoSerialisable {

    CPU cpu;

    /**
     * Handle an HWI instruction
     */
    public abstract void handleInterrupt(Status status);

    public CPU getCpu() {
        return cpu;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    public abstract char getId();

    public static CpuHardware deserialize(DBObject obj) {

        for (ServerPlugin plugin : GameServer.INSTANCE.getPluginManager().getPlugins()) {

            if (plugin instanceof CpuHardwareDeserializer) {
                CpuHardware hw = ((CpuHardwareDeserializer) plugin).deserializeHardware(obj);

                if (hw != null) {
                    return hw;
                }
            }
        }

        return null;
    }

}
