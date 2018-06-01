package net.simon987.server.assembly;


import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.io.MongoSerializable;
import org.bson.Document;


public abstract class HardwareModule implements MongoSerializable {

    private CPU cpu;

    public HardwareModule() {

    }

    public HardwareModule(Document document, ControllableUnit unit) {

    }

    /**
     * Handle an HWI instruction
     */
    public abstract void handleInterrupt(Status status);

    protected CPU getCpu() {
        return cpu;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    public abstract char getId();

    @Override
    public String toString() {
        return String.format("{%s}", getClass().getSimpleName());
    }
}
