package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import org.json.simple.JSONObject;

public class CubotKeyboard extends CpuHardware {

    public static final int DEFAULT_ADDRESS = 4;

    private static final int CLEAR_BUFFER = 0;
    private static final int FETCH_KEY = 1;

    /**
     * Hardware ID (Should be unique)
     */
    public static final char HWID = 0x0004;

    private Cubot cubot;

    public CubotKeyboard(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == CLEAR_BUFFER) {

            cubot.clearKeyboardBuffer();

        } else if (a == FETCH_KEY) {
            //pop
            int key = 0;
            if (cubot.getKeyboardBuffer().size() > 0) {
                key = cubot.getKeyboardBuffer().get(0);
                cubot.getKeyboardBuffer().remove(0);
            }

            getCpu().getRegisterSet().getRegister("B").setValue(key);

        }

    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();
        json.put("hwid", (int) HWID);
        json.put("cubot", cubot.getObjectId());

        return json;
    }

    public static CubotKeyboard deserialize(JSONObject hwJSON) {
        return new CubotKeyboard((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int) (long) hwJSON.get("cubot")));
    }
}
