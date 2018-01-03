package net.simon987.mischwplugin;

import com.mongodb.BasicDBObject;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;

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
    public BasicDBObject mongoSerialise() {

        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("hwid", (int) HWID);

        return dbObject;
    }

    public static RandomNumberGenerator deserialize() {
        return new RandomNumberGenerator();
    }
}
