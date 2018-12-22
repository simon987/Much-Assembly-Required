package net.simon987.cubotplugin;

import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.Action;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.world.Tile;
import org.bson.Document;

public class CubotDrill extends HardwareModule {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0005;

    public static final int DEFAULT_ADDRESS = 5;

    private static final int DRILL_POLL = 1;
    private static final int DRILL_GATHER = 2; // simplified gather

    public CubotDrill(ControllableUnit unit) {
        super(null, unit);
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

            if (unit.spendEnergy(1400)) {
                if (unit.getCurrentAction() == Action.IDLE) {

                    Tile tile = unit.getWorld().getTileMap().getTileAt(unit.getX(), unit.getY());

                    Item newItem = tile.drill();
                    if (newItem != null) {
                        unit.setCurrentAction(Action.DIGGING);
                        unit.giveItem(newItem);
                    }
                }
            }
        }
    }
}
