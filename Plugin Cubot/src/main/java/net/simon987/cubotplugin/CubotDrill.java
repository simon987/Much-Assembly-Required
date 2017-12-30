package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.Action;
import net.simon987.server.game.TileMap;
import org.json.simple.JSONObject;

public class CubotDrill extends CpuHardware {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0005;

    public static final int DEFAULT_ADDRESS = 5;

    private static final int DRILL_POLL = 1;
    private static final int DRILL_GATHER_SLOW = 2;
    private static final int DRILL_GATHER_FAST = 3;

    private Cubot cubot;

    public CubotDrill(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public void handleInterrupt(Status status) {
        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == DRILL_POLL) {

            getCpu().getRegisterSet().getRegister("B").setValue(0);

        } else if (a == DRILL_GATHER_SLOW || a == DRILL_GATHER_FAST) {

            if (cubot.spendEnergy(1400)) {
                if (cubot.getCurrentAction() == Action.IDLE) {
                    int tile = cubot.getWorld().getTileMap().getTileAt(cubot.getX(), cubot.getY());

                    if (tile == TileMap.IRON_TILE) {
                        cubot.setHeldItem(TileMap.ITEM_IRON);
                        cubot.setCurrentAction(Action.DIGGING);

                    } else if (tile == TileMap.COPPER_TILE) {
                        cubot.setHeldItem(TileMap.ITEM_COPPER);
                        cubot.setCurrentAction(Action.DIGGING);

                    }
                }
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

    public static CubotDrill deserialize(JSONObject hwJSON) {
        return new CubotDrill((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int) (long) hwJSON.get("cubot")));
    }
}
