package net.simon987.cubotplugin;

import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.MessageReceiver;
import org.bson.Document;

import java.awt.*;
import java.util.ArrayList;

public class CubotComPort extends CubotHardwareModule {

    public static final char HWID = 0xD;
    public static final int DEFAULT_ADDRESS = 0xD;

    private static final int COMPORT_BUFFER_CLEAR = 0;
    private static final int COMPORT_POLL = 1;
    private static final int COMPORT_FRONT_PORT_OUT = 2;
    private static final int COMPORT_SELF_OUT = 3;
    private static final int COMPORT_CONSOLE_CLEAR = 4;

    public CubotComPort(Cubot cubot) {
        super(cubot);
    }

    public CubotComPort(Document document, ControllableUnit cubot) {
        super(document, cubot);
    }

    private static final int MESSAGE_LENGTH = 8;

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == COMPORT_BUFFER_CLEAR) {

            cubot.getConsoleMessagesBuffer().clear();
            
        } else if (a == COMPORT_CONSOLE_CLEAR) {
            
            cubot.setConsoleMode(Cubot.ConsoleMode.CLEAR);

        } else if (a == COMPORT_POLL) {

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

        } else if (a == COMPORT_FRONT_PORT_OUT) {

            if (cubot.spendEnergy(20)) {
                //Get object directly in front of the Cubot
                Point frontTile = cubot.getFrontTile();
                //Todo will have to add getGameObjectsBlockingAt to enable Factory
                ArrayList<GameObject> objects = cubot.getWorld().getGameObjectsAt(frontTile.x, frontTile.y);

                if (objects.size() > 0 && objects.get(0) instanceof MessageReceiver) {

                    int x = getCpu().getRegisterSet().getRegister("X").getValue();

                    if (x + MESSAGE_LENGTH >= getCpu().getMemory().getWords().length) {
                        //todo set interrupt ?
                        getCpu().getStatus().setErrorFlag(true);
                    } else {

                        //Get MESSAGE_LENGTH-word message pointed by X
                        char[] message = new char[MESSAGE_LENGTH];
                        System.arraycopy(getCpu().getMemory().getWords(), x, message, 0, MESSAGE_LENGTH);

                        //Send it to the MessageReceiver object
                        getCpu().getRegisterSet().getRegister("B").setValue(
                                ((MessageReceiver) objects.get(0)).sendMessage(message) ? 1 : 0);
                        return;
                    }
                }
            }

            getCpu().getRegisterSet().getRegister("B").setValue(0); //Failed

        } else if (a == COMPORT_SELF_OUT) {

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
                    getCpu().getRegisterSet().getRegister("B").setValue(cubot.sendMessage(message) ? 1 : 0);
                    return;
                }
            }

            getCpu().getRegisterSet().getRegister("B").setValue(0); //Failed
        }
    }

    @Override
    public char getId() {
        return HWID;
    }
}
