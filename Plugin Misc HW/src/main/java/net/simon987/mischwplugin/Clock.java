package net.simon987.mischwplugin;

import com.mongodb.BasicDBObject;
import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Util;

public class Clock extends CpuHardware {

    public static final char HWID = 0x0008;

    public static final char DEFAULT_ADDRESS = 0x0008;

    @Override
    public void handleInterrupt(Status status) {

        int time = (int) GameServer.INSTANCE.getGameUniverse().getTime();

        //Will need to be changed to quadword in about 136 years
        getCpu().getRegisterSet().getRegister("B").setValue(Util.getHigherWord(time));
        getCpu().getRegisterSet().getRegister("C").setValue(Util.getLowerWord(time));

    }

    @Override
    public char getId() {
        return HWID;
    }

    public static Clock deserialize() {
        return new Clock();
    }


    @Override
    public BasicDBObject mongoSerialise() {

        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("hwid", (int) HWID);

        return dbObject;
    }
}
