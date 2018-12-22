package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.ControllableUnit;
import org.bson.Document;
import org.json.simple.JSONObject;

public class NpcBattery extends HardwareModule {

    public static final int DEFAULT_ADDRESS = 0x010A;

    /**
     * Hardware ID (Should be unique)
     */
    public static final char HWID = 0x010A;

    /**
     * Energy units in kJ
     */
    private int energy;

    /**
     * Maximum energy units in kJ
     */
    private int maxEnergy;


    private static final int BATTERY_POLL = 1;
    private static final int BATTERY_GET_MAX_CAPACITY = 2;

    public NpcBattery(ControllableUnit unit) {
        super(null, unit);

        energy = GameServer.INSTANCE.getConfig().getInt("battery_max_energy");
        maxEnergy = GameServer.INSTANCE.getConfig().getInt("battery_max_energy");
    }

    public NpcBattery(Document document, ControllableUnit cubot) {
        super(document, cubot);

        energy = document.getInteger("energy");
        maxEnergy = document.getInteger("max_energy");
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == BATTERY_POLL) {
            getCpu().getRegisterSet().getRegister("B").setValue(unit.getEnergy());

        } else if (a == BATTERY_GET_MAX_CAPACITY) {
            getCpu().getRegisterSet().getRegister("B").setValue(unit.getMaxEnergy());

        }
    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public JSONObject jsonSerialise() {
        JSONObject json = new JSONObject();

        json.put("energy", energy);

        return json;
    }

    @Override
    public JSONObject debugJsonSerialise() {
        JSONObject json = jsonSerialise();

        json.put("max_energy", maxEnergy);

        return json;
    }

    @Override
    public Document mongoSerialise() {
        Document document = super.mongoSerialise();

        document.put("energy", energy);
        document.put("max_energy", maxEnergy);

        return document;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }
}
