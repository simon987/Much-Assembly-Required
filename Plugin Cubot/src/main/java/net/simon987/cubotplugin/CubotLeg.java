package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.Action;
import net.simon987.server.game.Direction;
import net.simon987.server.io.JSONSerialisable;
import org.json.simple.JSONObject;

public class CubotLeg extends CpuHardware implements JSONSerialisable {

    public static final int DEFAULT_ADDRESS = 1;

    public static final String NAME = "Cubot Leg";

    private static final int LEGS_SET_DIR = 1;
    private static final int LEGS_SET_DIR_AND_WALK = 2;
    private static final int LEGS_JUMP = 3;

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0001;

    private Cubot cubot;

    public CubotLeg(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public void handleInterrupt(Status status) {
        int a = getCpu().getRegisterSet().getRegister("A").getValue();
        int b = getCpu().getRegisterSet().getRegister("B").getValue();

        if (a == LEGS_SET_DIR) {


            Direction dir = Direction.getDirection(b);

            if (dir != null) {
                if (cubot.spendEnergy(20)) {
                    cubot.setDirection(Direction.getDirection(b));
                    status.setErrorFlag(false);
                }
            } else {
                status.setErrorFlag(true);
            }


        } else if (a == LEGS_SET_DIR_AND_WALK) {

            if (cubot.getMaxEnergy() >= 100) {
                Direction dir = Direction.getDirection(b);

                if (dir != null) {
                    cubot.setDirection(Direction.getDirection(b));
                    status.setErrorFlag(false);
                } else {
                    status.setErrorFlag(true);
                }

                cubot.setCurrentAction(Action.WALKING);
            }
        } else if (a == LEGS_JUMP) {
            // jump up to 3 spaces, each space costs 200
            if (b < 4 && cubot.getMaxEnergy() >= b * 300) {
                cubot.setJumpDistance(b);
                cubot.setCurrentAction(Action.JUMPING);
                status.setErrorFlag(false);
            } else {
                status.setErrorFlag(true);
            }
        }
    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();
        json.put("hwid", (int) HWID);
        json.put("cubot", cubot.getObjectId());

        return json;
    }

    public static CubotLeg deserialize(JSONObject hwJSON) {
        return new CubotLeg((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int) (long) hwJSON.get("cubot")));
    }


}
