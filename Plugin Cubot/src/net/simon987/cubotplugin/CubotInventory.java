package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import org.json.simple.JSONObject;

public class CubotInventory extends CpuHardware {

    /**
     * Hardware ID (Should be unique)
     */
    static final int HWID = 0x0006;

    public static final int DEFAULT_ADDRESS = 6;

    private Cubot cubot;

    private static final int POLL = 1;
    private static final int CLEAR = 2;

    public CubotInventory(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if(a == POLL) {

            getCpu().getRegisterSet().getRegister("B").setValue(cubot.getHeldItem());

        } else if (a == CLEAR) {
            cubot.setHeldItem(0);
        }

    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();
        json.put("hwid", HWID);
        json.put("cubot", cubot.getObjectId());

        return json;
    }

    public static CubotInventory deserialize(JSONObject hwJSON){
        return new CubotInventory((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int)(long)hwJSON.get("cubot")));
    }
}
