package net.simon987.cubotplugin;

import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.Action;
import net.simon987.server.game.world.TileMap;
import org.bson.Document;

public class CubotDrill extends CubotHardware {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0005;

    public static final int DEFAULT_ADDRESS = 5;

    private static final int DRILL_POLL = 1;
    private static final int DRILL_GATHER = 2; // simplified gather

    public CubotDrill(Cubot cubot) {
        super(cubot);
    }

    public CubotDrill(Document document) {
        super(document);
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

        } else if (a == DRILL_GATHER) {

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
}
