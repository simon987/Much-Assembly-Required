package net.simon987.cubotplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;

public class CubotCore extends CpuHardware {

    public static final int DEFAULT_ADDRESS = 0x000E;

    /**
     * Hardware ID (Should be unique)
     */
    public static final char HWID = 0x000E;

    private static final int CORE_STATUS_POLL = 1;
    private static final int CORE_HULL_POLL = 2;

    private Cubot cubot;

    public CubotCore(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == CORE_STATUS_POLL) {
            getCpu().getRegisterSet().getRegister("B").setValue(cubot.getStatus());
        } else if (a == CORE_HULL_POLL) {
            getCpu().getRegisterSet().getRegister("B").setValue(cubot.getHp());
        }
    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public BasicDBObject mongoSerialise() {

        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("hwid", (int) HWID);
        dbObject.put("cubot", cubot.getObjectId());

        return dbObject;
    }


    public static CubotCore deserialize(DBObject obj) {
        return new CubotCore((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((long) obj.get("cubot")));
    }
}
