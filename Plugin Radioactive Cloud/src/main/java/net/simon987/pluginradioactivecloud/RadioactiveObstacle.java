package net.simon987.pluginradioactivecloud;

import net.simon987.server.GameServer;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.Enterable;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.Radioactive;

public class RadioactiveObstacle extends GameObject implements Radioactive, Enterable {

	private final static int corruptionBlockSize = GameServer.INSTANCE.getConfig().getInt("radioactive_obstacle_corruption_block_size");
	private final static int MAP_INFO = 0x0A01; //10
	
	@Override
	public char getMapInfo() {
		return MAP_INFO;
	}

	@Override
	public boolean enter(GameObject object) {
		if (object instanceof ControllableUnit) {
			((ControllableUnit) object).getCpu().getMemory().corrupt(corruptionBlockSize);
		}
		return false;
	}
}