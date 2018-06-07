package net.simon987.npcplugin;

import org.bson.Document;

import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.Enterable;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.Radioactive;

public class RadioactiveObstacle extends GameObject implements Radioactive, Enterable {

	private int corruptionBlockSize;

	public RadioactiveObstacle(int corruptionBlockSize) {
		this.corruptionBlockSize = corruptionBlockSize;
	}

	@Override
	public char getMapInfo() {
		// TODO I don't know how this should be done.
		return 0;
	}

	@Override
	public boolean enter(GameObject object) {
		if (object instanceof ControllableUnit)
			((ControllableUnit) object).getCpu().getMemory().corrupt(corruptionBlockSize);
		return false;
	}

	public Document mongoSerialize() {
		Document dbObject = super.mongoSerialise();

		dbObject.put("corruptionBlockSize", corruptionBlockSize);

		return dbObject;
	}

	public void setBlockSize(int corruptionBlockSize) {
		this.corruptionBlockSize = corruptionBlockSize;
	}

	public int getBlockSize() {
		return corruptionBlockSize;
	}
}
