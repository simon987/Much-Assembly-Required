package net.simon987.mar.cubot;

import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.assembly.HardwareModule;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Util;
import net.simon987.mar.server.game.objects.ControllableUnit;
import org.bson.Document;

/**
 * Hardware to get game time
 */
public class Clock extends HardwareModule {

    private static final char HWID = 0x0008;

    public static final char DEFAULT_ADDRESS = 0x0008;

    public Clock() {

    }

    public Clock(Document document, ControllableUnit unit) {
        super(document, unit);
    }

    @Override
    public void handleInterrupt(Status status) {

        int time = (int) GameServer.INSTANCE.getUniverse().getTime();

        //Will need to be changed to quadword in about 136 years
        getCpu().getRegisterSet().getRegister("B").setValue(Util.getHigherWord(time));
        getCpu().getRegisterSet().getRegister("C").setValue(Util.getLowerWord(time));

    }

    @Override
    public char getId() {
        return HWID;
    }


    @Override
    public Document mongoSerialise() {

        Document dbObject = new Document();
        dbObject.put("type", getClass().getCanonicalName());

        return dbObject;
    }
}
