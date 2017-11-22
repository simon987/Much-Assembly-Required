package net.simon987.mischwplugin;

import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import org.json.simple.JSONObject;

import java.util.Random;

public class RandomNumberGenerator extends CpuHardware {

    public static final char HWID = 0x0007;

    public static final char DEFAULT_ADDRESS = 0x0007;

    private Random random;

    public RandomNumberGenerator() {
        random = new Random();
    }

    @Override
    public void handleInterrupt(Status status) {

        getCpu().getRegisterSet().getRegister("B").setValue(random.nextInt(0xFFFF));

    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();
        json.put("hwid", (int) HWID);

        return json;
    }

    public static RandomNumberGenerator deserialize(JSONObject hwJSON) {
        return new RandomNumberGenerator();
    }
}
