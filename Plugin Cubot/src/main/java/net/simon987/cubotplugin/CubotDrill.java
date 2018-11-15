package net.simon987.cubotplugin;

import net.simon987.server.assembly.Status;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.Action;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.world.Tile;
import org.bson.Document;

public class CubotDrill extends CubotHardwareModule {

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

    public CubotDrill(Document document, ControllableUnit cubot) {
        super(document, cubot);
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

                    Tile tile = cubot.getWorld().getTileMap().getTileAt(cubot.getX(), cubot.getY());

                    Item newItem = tile.drill();
                    if (newItem != null) {
                        cubot.setCurrentAction(Action.DIGGING);
                        cubot.giveItem(newItem);
                    }
                }
            }
        }
    }
}
