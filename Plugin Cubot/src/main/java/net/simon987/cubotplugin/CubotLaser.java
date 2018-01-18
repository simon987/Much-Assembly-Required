package net.simon987.cubotplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.Action;
import net.simon987.server.game.Attackable;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.InventoryHolder;

import java.awt.*;
import java.util.ArrayList;

public class CubotLaser extends CpuHardware {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0002;

    public static final int DEFAULT_ADDRESS = 2;

    private Cubot cubot;

    private static final int LASER_WITHDRAW = 1;
    private static final int LASER_DEPOSIT = 2;
    private static final int LASER_ATTACK = 3;

    private static final int LASER_DAMAGE = 25;

    public CubotLaser(Cubot cubot) {
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


        if (a == LASER_WITHDRAW) {


            Point frontTile = cubot.getFrontTile();
            ArrayList<GameObject> objects = cubot.getWorld().getGameObjectsBlockingAt(frontTile.x, frontTile.y);

            if (cubot.getCurrentAction() == Action.IDLE && objects.size() > 0) {
                //FIXME: Problem here if more than 1 object
                if (objects.get(0) instanceof InventoryHolder) {
                    if (((InventoryHolder) objects.get(0)).canTakeItem(b)) {
                        if (cubot.spendEnergy(30)) {
                            //Take the item
                            ((InventoryHolder) objects.get(0)).takeItem(b);

                            cubot.setHeldItem(b);
                            cubot.setCurrentAction(Action.WITHDRAWING);
                        }
                    }
                }
            }


        } else if (a == LASER_DEPOSIT) {
            // TODO
        } else if (a == LASER_ATTACK) {

            if (cubot.spendEnergy(70)) {
                
                //Get object directly in front of the Cubot
                Point frontTile = cubot.getFrontTile();
                ArrayList<GameObject> objects = cubot.getWorld().getGameObjectsAt(frontTile.x, frontTile.y);

                if (objects.size() > 0 && objects.get(0) instanceof Attackable) {
                    ((Attackable) objects.get(0)).damage(LASER_DAMAGE);
                }

            }

        }

    }

    @Override
    public BasicDBObject mongoSerialise() {

        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("hwid", (int) HWID);
        dbObject.put("cubot", cubot.getObjectId());

        return dbObject;
    }

    public static CubotLaser deserialize(DBObject obj) {
        return new CubotLaser((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((long) obj.get("cubot")));
    }
}
