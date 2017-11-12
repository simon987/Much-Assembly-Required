package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import org.json.simple.JSONObject;

public class CubotHologram extends CpuHardware {


    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0009;

    public static final int DEFAULT_ADDRESS = 9;

    private Cubot cubot;

    public CubotHologram(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public void handleInterrupt(Status status) {

        char a = getCpu().getRegisterSet().getRegister("A").getValue();
        cubot.setHologram(a);

    }

    @Override
    public char getId() {
        return HWID;
    }

    public static CubotHologram deserialize(JSONObject hwJSON) {
        return new CubotHologram((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int) (long) hwJSON.get("cubot")));
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();
        json.put("hwid", (int) HWID);
        json.put("cubot", cubot.getObjectId());

        return json;
    }
}
