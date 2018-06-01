package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.ControllableUnit;
import org.bson.Document;

public class CubotShield extends CubotHardwareModule {

    public static final char DEFAULT_ADDRESS = 0x000F;

    static final char HWID = 0x000F;

    private static final int SHIELD_CHARGE = 1;
    private static final int SHIELD_POLL = 2;

    public static final int COST = GameServer.INSTANCE.getConfig().getInt("shield_energy_cost");

    public CubotShield(Cubot cubot) {
        super(cubot);
    }

    public CubotShield(Document document, ControllableUnit cubot) {
        super(document, cubot);
    }

    @Override
    public char getId() {
        return HWID;
    }

	@Override
	public void handleInterrupt(Status status) {
        int a = getCpu().getRegisterSet().getRegister("A").getValue();
        // b = amount to charge
        if(a == SHIELD_CHARGE) {
            int b = getCpu().getRegisterSet().getRegister("B").getValue();
            cubot.chargeShield(b);
        } else if (a == SHIELD_POLL) {
            int shield = cubot.getShield();
            getCpu().getRegisterSet().getRegister("B").setValue(shield);
        }
    }
}