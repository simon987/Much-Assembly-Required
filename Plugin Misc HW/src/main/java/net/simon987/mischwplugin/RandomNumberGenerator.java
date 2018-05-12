package net.simon987.mischwplugin;

import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import org.bson.Document;

import java.util.Random;

/**
 * Hardware to generate random numbers
 */
public class RandomNumberGenerator extends CpuHardware {

    private static final char HWID = 0x0007;

    public static final char DEFAULT_ADDRESS = 0x0007;

    private Random random;

    public RandomNumberGenerator() {
        random = new Random();
    }

    public RandomNumberGenerator(Document document) {
        super(document);
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
    public Document mongoSerialise() {

        Document dbObject = new Document();
        dbObject.put("type", getClass().getCanonicalName());

        return dbObject;
    }
}
