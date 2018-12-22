package net.simon987.cubotplugin;

import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.Action;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.Direction;
import org.bson.Document;

public class CubotLeg extends HardwareModule {

    public static final int DEFAULT_ADDRESS = 1;

    private static final int LEGS_SET_DIR = 1;
    private static final int LEGS_SET_DIR_AND_WALK = 2;

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0001;

    public CubotLeg(ControllableUnit unit) {
        super(null, unit);
    }

    public CubotLeg(Document document, ControllableUnit unit) {
        super(document, unit);
    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public void handleInterrupt(Status status) {

        if (unit.getCurrentAction() == Action.IDLE) {
            int a = getCpu().getRegisterSet().getRegister("A").getValue();
            int b = getCpu().getRegisterSet().getRegister("B").getValue();

            if (a == LEGS_SET_DIR) {


                Direction dir = Direction.getDirection(b);

                if (dir != null) {
                    if (unit.spendEnergy(20)) {
                        unit.setDirection(Direction.getDirection(b));
                        status.setErrorFlag(false);
                    }
                } else {
                    status.setErrorFlag(true);
                }


            } else if (a == LEGS_SET_DIR_AND_WALK) {

                if (unit.getMaxEnergy() >= 100) {
                    Direction dir = Direction.getDirection(b);

                    if (dir != null) {
                        unit.setDirection(Direction.getDirection(b));
                        status.setErrorFlag(false);
                    } else {
                        status.setErrorFlag(true);
                    }

                    unit.setCurrentAction(Action.WALKING);
                }
            }
        }
    }
}
