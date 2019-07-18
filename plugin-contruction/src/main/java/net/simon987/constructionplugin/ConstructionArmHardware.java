package net.simon987.constructionplugin;

import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.assembly.Register;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.GameObject;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.awt.*;

public class ConstructionArmHardware extends HardwareModule {

    public static final char HWID = 0x0010;

    public static final int DEFAULT_ADDRESS = 0x0010;

    private static final int OK = 0;
    private static final int ERR_TILE_BLOCKED = 1;
    private static final int ERR_NOT_ENOUGH_ENERGY = 2;
    private static final int ERR_MEM_READ = 3;
    private static final int ERR_INVALID_BLUEPRINT = 4;

    private static final int PLACE_CONSTRUCTION_SITE = 1;
    private static final int PLACE_CONSTRUCTION_SITE_COST = 10;


    public ConstructionArmHardware(ControllableUnit unit) {
        super(null, unit);
    }

    public ConstructionArmHardware(Document document, ControllableUnit unit) {
        super(document, unit);
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();
        int x = getCpu().getRegisterSet().getRegister("X").getValue();
        Register regB = getCpu().getRegisterSet().getRegister("B");

        if (a == PLACE_CONSTRUCTION_SITE) {
            char[] bluePrintData = getCpu().getMemory().read(x, BluePrint.DATA_LENGTH);

            if (bluePrintData == null) {
                regB.setValue(ERR_MEM_READ);
                return;
            }

            BluePrint bluePrint = BluePrintRegistry.INSTANCE.deserializeBluePrint(bluePrintData);
            if (bluePrint == null) {
                regB.setValue(ERR_INVALID_BLUEPRINT);
                return;
            }

            Point frontTile = unit.getFrontTile();

            if (unit.getWorld().isTileBlocked(frontTile.x, frontTile.y) ||
                    unit.getWorld().canBuild(frontTile.x, frontTile.y)) {
                regB.setValue(ERR_TILE_BLOCKED);
                return;
            }

            if (!unit.spendEnergy(PLACE_CONSTRUCTION_SITE_COST)) {
                regB.setValue(ERR_NOT_ENOUGH_ENERGY);
                return;
            }

            GameObject constructionSite = new ConstructionSite(bluePrint);
            constructionSite.setObjectId(new ObjectId());
            constructionSite.setWorld(unit.getWorld());
            constructionSite.setX(frontTile.x);
            constructionSite.setY(frontTile.y);

            unit.getWorld().addObject(constructionSite);
            regB.setValue(OK);
        }
    }

    @Override
    public char getId() {
        return HWID;
    }
}

