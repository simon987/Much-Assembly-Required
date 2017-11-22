package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import org.json.simple.JSONObject;

public class CubotBattery extends CpuHardware {

    public static final int DEFAULT_ADDRESS = 0x000A;

    /**
     * Hardware ID (Should be unique)
     */
    public static final char HWID = 0x000A;

    private Cubot cubot;
    private static final int POLL = 1;
    private static final int GET_MAX_CAPACITY = 2;

    public CubotBattery(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == POLL) {
            getCpu().getRegisterSet().getRegister("B").setValue(cubot.getEnergy());

        } else if (a == GET_MAX_CAPACITY) {
            getCpu().getRegisterSet().getRegister("B").setValue(cubot.getMaxEnergy());
        } else if (a == 0xFFFF) {
            cubot.setEnergy(cubot.getMaxEnergy());
        }

    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();
        json.put("hwid", (int) HWID);
        json.put("cubot", cubot.getObjectId());

        return json;
    }

    public static CubotBattery deserialize(JSONObject hwJSON) {
        return new CubotBattery((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int) (long) hwJSON.get("cubot")));
    }

}
