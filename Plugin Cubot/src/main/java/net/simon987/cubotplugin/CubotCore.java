package net.simon987.cubotplugin;

import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.ControllableUnit;
import org.bson.Document;

public class CubotCore extends CubotHardwareModule {

    public static final int DEFAULT_ADDRESS = 0x000E;

    /**
     * Hardware ID (Should be unique)
     */
    public static final char HWID = 0x000E;

    private static final int CORE_STATUS_POLL = 1;
    private static final int CORE_HULL_POLL = 2;

    public CubotCore(Cubot cubot) {
        super(cubot);
    }

    public CubotCore(Document document, ControllableUnit cubot) {
        super(document, cubot);
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == CORE_STATUS_POLL) {
            getCpu().getRegisterSet().getRegister("B").setValue(cubot.getStatus());
        } else if (a == CORE_HULL_POLL) {
            getCpu().getRegisterSet().getRegister("B").setValue(cubot.getHp());
        }
    }

    @Override
    public char getId() {
        return HWID;
    }
}
