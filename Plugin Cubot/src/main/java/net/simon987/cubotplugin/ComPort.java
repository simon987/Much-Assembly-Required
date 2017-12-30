package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.Programmable;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;

public class ComPort extends CpuHardware {

    public static final char HWID = 0xD;
    public static final int DEFAULT_ADDRESS = 0xD;

    private Cubot cubot;

    private static final int POLL = 1;

    private static final int OUT = 2;

    public ComPort(Cubot cubot) {
        this.cubot = cubot;
    }

    private static final int MESSAGE_LENGTH = 8;

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == POLL) {

            /* No-op */

        } else if (a == OUT) {

            //Get object directly in front of the Cubot
            Point frontTile = cubot.getFrontTile();
            ArrayList<GameObject> objects = cubot.getWorld().getGameObjectsBlockingAt(frontTile.x, frontTile.y);

            if (objects.size() > 0 && objects.get(0) instanceof Programmable) {

                int x = getCpu().getRegisterSet().getRegister("X").getValue();

                if (x + MESSAGE_LENGTH >= getCpu().getMemory().getWords().length) {
                    //todo set interrupt ?
                    getCpu().getStatus().setErrorFlag(true);
                } else {

                    //Get MESSAGE_LENGTH-word message pointed by X
                    char[] message = new char[MESSAGE_LENGTH];
                    System.arraycopy(getCpu().getMemory().getWords(), x, message, 0, MESSAGE_LENGTH);

                    //Send it to the Programmable object
                    ((Programmable) objects.get(0)).sendMessage(message);
                }
            }
        }


    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();
        json.put("hwid", (int) HWID);
        json.put("cubot", cubot.getObjectId());

        return json;
    }

    public static ComPort deserialize(JSONObject json) {
        return new ComPort((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int) (long) json.get("cubot")));
    }
}
