package net.simon987.server.assembly;


import net.simon987.server.GameServer;
import net.simon987.server.io.CpuHardwareDeserializer;
import net.simon987.server.io.JSONSerialisable;
import net.simon987.server.plugin.ServerPlugin;
import org.json.simple.JSONObject;

public abstract class CpuHardware implements JSONSerialisable {

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

    public static CpuHardware deserialize(JSONObject hwJson){

        for(ServerPlugin plugin : GameServer.INSTANCE.getPluginManager().getPlugins()){

            if(plugin instanceof CpuHardwareDeserializer){
                CpuHardware hw = ((CpuHardwareDeserializer) plugin).deserializeHardware(hwJson);

                if(hw != null){
                    return hw;
                }
            }
        }

        return null;
    }

}
