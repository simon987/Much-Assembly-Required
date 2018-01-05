package net.simon987.cubotplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;

public class CubotShield extends CpuHardware {
    static final char HWID = 0x000E;

    private static final int SHIELD_CHARGE = 1;
    private static final int SHIELD_QUERY = 2;
    private Cubot cubot;

    public CubotShield(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public char getId() {
        return HWID;
    }

	@Override
	public BasicDBObject mongoSerialise() {
        BasicDBObject dbObject = new BasicDBObject();
        
        dbObject.put("hwid", HWID);
        dbObject.put("cubot", cubot.getObjectId());
        
        return dbObject;
	}

	@Override
	public void handleInterrupt(Status status) {
        int a = getCpu().getRegisterSet().getRegister("A").getValue();
        // b = amount to charge
        if(a == SHIELD_CHARGE) {
            int b = getCpu().getRegisterSet().getRegister("B").getValue();
            cubot.chargeShield(b);
        } else if(a == SHIELD_QUERY) {
            int shield = cubot.getShield();
            getCpu().getRegisterSet().getRegister("B").setValue(shield);
        }
    }
    
    public static CubotShield deserialize(DBObject obj) {
        return new CubotShield((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((long) obj.get("cubot")));
    }
}