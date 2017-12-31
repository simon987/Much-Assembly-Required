package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.Action;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.InventoryHolder;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;

public class CubotLaser extends CpuHardware {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0002;

    public static final int DEFAULT_ADDRESS = 2;

    private Cubot cubot;

    private static final int WITHDRAW = 1;
    private static final int DEPOSIT = 2;


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


        if (a == WITHDRAW) {


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


        } else if (a == DEPOSIT) {

        }

    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();
        json.put("hwid", (int) HWID);
        json.put("cubot", cubot.getObjectId());

        return json;
    }

    public static CubotLaser deserialize(JSONObject hwJSON) {
        return new CubotLaser((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int) (long) hwJSON.get("cubot")));
    }
}
