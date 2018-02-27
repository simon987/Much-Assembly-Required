package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.Updatable;

public class ElectricBox extends Obstacle implements Updatable {

    public static final int STYLE = 1;

    private static final int maxHp = GameServer.INSTANCE.getConfig().getInt("electric_box_hp");
    private static final int damage = GameServer.INSTANCE.getConfig().getInt("electric_box_damage");
    private static final int energyGiven = GameServer.INSTANCE.getConfig().getInt("electric_box_energy_given");

    public ElectricBox() {
        super(maxHp);

        setStyle(STYLE);
    }

    @Override
    public void update() {

    }

    @Override
    public boolean onDeadCallback() {
        getWorld().decUpdatable();
        return false;
    }
}
