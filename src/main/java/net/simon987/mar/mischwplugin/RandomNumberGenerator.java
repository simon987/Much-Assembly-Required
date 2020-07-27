package net.simon987.mar.mischwplugin;

import net.simon987.mar.server.assembly.HardwareModule;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.game.objects.ControllableUnit;
import org.bson.Document;

import java.util.Random;

/**
 * Hardware to generate random numbers
 */
public class RandomNumberGenerator extends HardwareModule {

    private static final char HWID = 0x0007;

    public static final char DEFAULT_ADDRESS = 0x0007;

    private final Random random;

    public RandomNumberGenerator() {
        random = new Random();
    }

    public RandomNumberGenerator(Document document, ControllableUnit unit) {
        super(document, unit);
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
