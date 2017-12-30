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

    private static final int SELF_CLEAR = 0;
    private static final int POLL = 1;
    private static final int FRONT_PORT_OUT = 2;
    private static final int SELF_OUT = 3;

    public ComPort(Cubot cubot) {
        this.cubot = cubot;
    }

    private static final int MESSAGE_LENGTH = 8;

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == SELF_CLEAR) {

            cubot.getConsoleMessagesBuffer().clear();
            cubot.setConsoleMode(Cubot.ConsoleMode.CLEAR);

        } else if (a == POLL) {

            if (cubot.spendEnergy(4)) {

                int x = getCpu().getRegisterSet().getRegister("X").getValue();

                //Read all messages in the console buffer to memory at X

                for (char[] message : cubot.getConsoleMessagesBuffer()) {
                    if (x + MESSAGE_LENGTH >= getCpu().getMemory().getWords().length) {
                        //todo set interrupt ?
                        getCpu().getStatus().setErrorFlag(true);
                    } else {
                        System.arraycopy(message, 0, getCpu().getMemory().getWords(), x, MESSAGE_LENGTH);
                    }
                }

                //Set B = number of messages
                getCpu().getRegisterSet().getRegister("B").setValue(cubot.getConsoleMessagesBuffer().size());

            }

        } else if (a == FRONT_PORT_OUT) {

            if (cubot.spendEnergy(20)) {
                //Get object directly in front of the Cubot
                Point frontTile = cubot.getFrontTile();
                //Todo will have to add getGameObjectsBlockingAt to enable Factory
                ArrayList<GameObject> objects = cubot.getWorld().getGameObjectsAt(frontTile.x, frontTile.y);

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

                        System.out.println("Sent message to " + ((Cubot) objects.get(0)).getParent().getUsername());
                    }
                }
            }

        } else if (a == SELF_OUT) {

            if (cubot.spendEnergy(1)) {

                int x = getCpu().getRegisterSet().getRegister("X").getValue();

                //Write a single message to console buffer
                if (x + MESSAGE_LENGTH >= getCpu().getMemory().getWords().length) {
                    //todo set interrupt ?
                    getCpu().getStatus().setErrorFlag(true);
                } else {

                    //Get MESSAGE_LENGTH-word message pointed by X
                    char[] message = new char[MESSAGE_LENGTH];
                    System.arraycopy(getCpu().getMemory().getWords(), x, message, 0, MESSAGE_LENGTH);
                    cubot.sendMessage(message);
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
