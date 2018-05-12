package net.simon987.cubotplugin;

import net.simon987.server.assembly.Status;
import org.bson.Document;

public class CubotBattery extends CubotHardware {

    public static final int DEFAULT_ADDRESS = 0x000A;

    /**
     * Hardware ID (Should be unique)
     */
    public static final char HWID = 0x000A;

    private static final int BATTERY_POLL = 1;
    private static final int BATTERY_GET_MAX_CAPACITY = 2;

    public CubotBattery(Cubot cubot) {
        super(cubot);
    }

    public CubotBattery(Document document) {
        super(document);
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == BATTERY_POLL) {
            getCpu().getRegisterSet().getRegister("B").setValue(cubot.getEnergy());

        } else if (a == BATTERY_GET_MAX_CAPACITY) {
            getCpu().getRegisterSet().getRegister("B").setValue(cubot.getMaxEnergy());
        } else if (a == 0xFFFF) {
            cubot.setEnergy(cubot.getMaxEnergy());
        }

    }

    @Override
    public char getId() {
        return HWID;
    }

}
