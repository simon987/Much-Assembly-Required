package net.simon987.server.assembly;


import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.io.JSONSerialisable;
import net.simon987.server.io.MongoSerializable;
import org.bson.Document;
import org.json.simple.JSONObject;


public abstract class HardwareModule implements MongoSerializable, JSONSerialisable {

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
        JSONObject hwJson = jsonSerialise();
        return String.format("{%s: {%s}}", getClass().getSimpleName(), hwJson == null ? "" : hwJson);
    }

    public void reset() {

    }

    public void update() {

    }

    @Override
    public JSONObject jsonSerialise() {
        return null;
    }

    @Override
    public JSONObject debugJsonSerialise() {
        return null;
    }
}
