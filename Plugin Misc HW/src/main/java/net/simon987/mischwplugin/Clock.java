package net.simon987.mischwplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Util;
import org.json.simple.JSONObject;

public class Clock extends CpuHardware {

    public static final char HWID = 0x0008;

    public static final char DEFAULT_ADDRESS = 0x0008;

    @Override
    public void handleInterrupt(Status status) {

        int time = (int) GameServer.INSTANCE.getGameUniverse().getTime();

        //Will need to be changed to quadword in about 136 years
        getCpu().getRegisterSet().getRegister("B").setValue(Util.getHigherWord(time));
        getCpu().getRegisterSet().getRegister("C").setValue(Util.getLowerWord(time));

    }

    @Override
    public char getId() {
        return HWID;
    }

    public static Clock deserialize(JSONObject hwJSON) {
        return new Clock();
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();
        json.put("hwid", (int) HWID);

        return json;
    }
}
