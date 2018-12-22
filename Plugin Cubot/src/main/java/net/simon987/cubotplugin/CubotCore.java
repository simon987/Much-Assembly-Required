package net.simon987.cubotplugin;

import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.ControllableUnit;
import org.bson.Document;

public class CubotCore extends HardwareModule {

    public static final int DEFAULT_ADDRESS = 0x000E;

    /**
     * Hardware ID (Should be unique)
     */
    public static final char HWID = 0x000E;

    private static final int CORE_STATUS_POLL = 1;
    private static final int CORE_HULL_POLL = 2;

    public CubotCore(ControllableUnit unit) {
        super(null, unit);
    }

    public CubotCore(Document document, ControllableUnit unit) {
        super(document, unit);
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == CORE_STATUS_POLL) {
            if (unit instanceof Cubot) {
                getCpu().getRegisterSet().getRegister("B").setValue(((Cubot) unit).getStatus());
            }
        } else if (a == CORE_HULL_POLL) {
            getCpu().getRegisterSet().getRegister("B").setValue(unit.getHp());
        }
    }

    @Override
    public char getId() {
        return HWID;
    }
}
