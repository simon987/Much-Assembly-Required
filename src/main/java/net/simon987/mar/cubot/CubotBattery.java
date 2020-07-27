package net.simon987.mar.cubot;

import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.assembly.HardwareModule;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.game.objects.ControllableUnit;
import org.bson.Document;
import org.json.simple.JSONObject;

public class CubotBattery extends HardwareModule {

    public static final int DEFAULT_ADDRESS = 0x000A;

    /**
     * Hardware ID (Should be unique)
     */
    public static final char HWID = 0x000A;
    /**
     * Solar panel multiplier
     * <br>TODO: Set this constant in dimension
     */
    private static final float SOLAR_PANEL_MULTIPLIER = 1;

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

    public CubotBattery(ControllableUnit unit) {
        super(null, unit);

        energy = GameServer.INSTANCE.getConfig().getInt("battery_max_energy");
        maxEnergy = GameServer.INSTANCE.getConfig().getInt("battery_max_energy");
    }

    public CubotBattery(Document document, ControllableUnit cubot) {
        super(document, cubot);

        energy = document.getInteger("energy");
        maxEnergy = document.getInteger("max_energy");
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == BATTERY_POLL) {
            getCpu().getRegisterSet().getRegister("B").setValue(energy);

        } else if (a == BATTERY_GET_MAX_CAPACITY) {
            getCpu().getRegisterSet().getRegister("B").setValue(maxEnergy);

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

    @Override
    public void update() {
        energy = Math.min(maxEnergy,
                energy + (int) (SOLAR_PANEL_MULTIPLIER * GameServer.INSTANCE.getDayNightCycle().getSunIntensity()));
    }
}
