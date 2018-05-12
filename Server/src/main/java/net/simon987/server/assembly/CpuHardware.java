package net.simon987.server.assembly;


import net.simon987.server.io.MongoSerializable;
import org.bson.Document;


public abstract class CpuHardware implements MongoSerializable {

    private CPU cpu;

    public CpuHardware() {

    }

    public CpuHardware(Document document) {

    }

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

    @Override
    public String toString() {
        return String.format("<%04X>", (int) getId());
    }
}
