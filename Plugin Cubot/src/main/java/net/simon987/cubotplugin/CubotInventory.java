package net.simon987.cubotplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;

public class CubotInventory extends CpuHardware {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0006;

    public static final int DEFAULT_ADDRESS = 6;

    private Cubot cubot;

    private static final int INV_CLEAR = 0;
    private static final int INV_POLL = 1;

    public CubotInventory(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == INV_POLL) {
            getCpu().getRegisterSet().getRegister("B").setValue(cubot.getHeldItem());

        } else if (a == INV_CLEAR) {
            if (cubot.getHeldItem() == 0x0001) {
                int energy = GameServer.INSTANCE.getConfig().getInt("biomassEnergyValue");
                cubot.storeEnergy(energy);
                cubot.setHeldItem(0);

            } else if (cubot.spendEnergy(100)) {
                cubot.setHeldItem(0);
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

    public static CubotInventory deserialize(DBObject obj) {
        return new CubotInventory((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((long) obj.get("cubot")));
    }
}
