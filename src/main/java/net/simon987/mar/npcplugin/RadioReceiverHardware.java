package net.simon987.mar.npcplugin;

import net.simon987.mar.server.assembly.HardwareModule;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Util;
import net.simon987.mar.server.game.objects.Action;
import net.simon987.mar.server.game.objects.ControllableUnit;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class RadioReceiverHardware extends HardwareModule {

    public static final char HWID = 0xC; //12

    private static final int LISTEN = 1;

    public static final int DEFAULT_ADDRESS = 0xC;

    private final ControllableUnit cubot;

    public RadioReceiverHardware(ControllableUnit cubot) {
        this.cubot = cubot;
    }

    public RadioReceiverHardware(Document document, ControllableUnit unit) {
        super(document, unit);

        this.cubot = unit;
    }

    @Override
    public void handleInterrupt(Status status) {
        int x = getCpu().getRegisterSet().getRegister("X").getValue();
        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == LISTEN) {

            //Find the nearest Radio Tower and query it
            cubot.setAction(Action.LISTENING);

            List<char[]> messages = new ArrayList<>(6);

            for (String world : NpcPlugin.settlementMap.keySet()) {
                RadioTower tower = NpcPlugin.settlementMap.get(world).getRadioTower();

                if (tower != null && Util.manhattanDist(
                        tower.getWorld().getX(), tower.getWorld().getY(),
                        cubot.getWorld().getX(), cubot.getWorld().getY()) <= RadioTower.MAX_RANGE) {
                    //Tower is in range
                    messages.addAll(tower.getMessages());
                }
            }

            //Write messages to memory
            int offset = 0;

            for (char[] message : messages) {

                getCpu().getMemory().write(x + offset, message, 0, message.length);
                offset += message.length;
            }

            //Write the amount of messages received to B
            getCpu().getRegisterSet().getRegister("B").setValue(messages.size());
        }
    }

    @Override
    public char getId() {
        return HWID;
    }


    @Override
    public Document mongoSerialise() {

        Document dbObject = new Document();

        dbObject.put("type", getClass().getCanonicalName());
        dbObject.put("cubot", cubot.getObjectId());

        return dbObject;
    }
}
