package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
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


        if(a == WITHDRAW) {

            //System.out.println("withdraw");

            Point frontTile = cubot.getFrontTile();
            ArrayList<GameObject> objects = cubot.getWorld().getGameObjectsAt(frontTile.x, frontTile.y);


            if (cubot.getAction() != CubotAction.IDLE && objects.size() > 0) {

                if (objects.get(0) instanceof InventoryHolder) {
                    //Take the item
                    if (((InventoryHolder) objects.get(0)).takeItem(b)) {

                        cubot.setHeldItem(b);
                        //System.out.println("took " + b);
                        cubot.setCurrentAction(CubotAction.WITHDRAWING);

                    } else {
                        //The inventory holder can't provide this item
                        //todo Add emote here
                        //  System.out.println("DEBUG: FAILED: take (The inventory holder can't provide this item)");
                    }

                }
            } else {
                //Nothing in front
                //    System.out.println("DEBUG: FAILED: take (Nothing in front or Cubot is busy)");
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

    public static CubotLaser deserialize(JSONObject hwJSON){
        return new CubotLaser((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int)(long)hwJSON.get("cubot")));
    }
}
