package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Register;
import org.json.simple.JSONObject;

public class CubotRepair extends CpuHardware {

    /**
     * Hardware ID (Should be unique)
     */
    public static final int HWID = 0x000C;

    /**
     * If cubot has iron, increase HP, spend energy and iron.
     */
    public static final int REPAIR_FIX = 1;
    /**
     * Set b to amount of HP.
     */
    public static final int REPAIR_GET_HP = 2;

    private Cubot cubot;

    public CubotRepair(Cubot cubot) {
        this.cubot = cubot;
    }

	@Override
	public JSONObject serialise() {
        JSONObject json = new JSONObject();
        json.put("hwid", HWID);
        json.put("cubot", cubot.getObjectId());

        return json;
	}

	@Override
	public void handleInterrupt(Status status) {
        int a = getCpu().getRegisterSet().getRegister("A").getValue();
        Register b = getCpu().getRegisterSet().getRegister("B");

        if(a == REPAIR_FIX) {
            int repairCost = GameServer.INSTANCE.getConfig().getInt("repairEnergyCost");
            int repairAmount = GameServer.INSTANCE.getConfig().getInt("repairHealing");
            if(cubot.getHeldItem() == 0x0003) { // cubot is holding iron
                if(cubot.spendEnergy(repairCost)) {
                    cubot.heal(repairAmount);
                }
            }
        }
        else if(a == REPAIR_GET_HP) {
            b.setValue(cubot.getHp());
        }
	}

	@Override
	public char getId() {
		return HWID;
	}

    public static CubotRepair deserialize(JSONObject json) {
        return new CubotRepair((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int) (long) json.get("cubot")));
    }
}