package net.simon987.pluginradioactivecloud;

import net.simon987.server.GameServer;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.Enterable;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.Radioactive;

public class RadioactiveCloud extends GameObject implements Radioactive, Enterable {
    private final static int CORRUPTION_BLOCK_SIZE =
            GameServer.INSTANCE.getConfig().getInt("radioactive_cloud_corruption_block_size");

    /**
     * Called when an object attempts to walk directly into a Enterable object
     *
     * @param object The game object that attempted to enter
     * @return true if successful, false to block the object
     */
    @Override
    public boolean enter(GameObject object) {
        if (object instanceof ControllableUnit) {
            ((ControllableUnit) object).getCpu().getMemory().corrupt(CORRUPTION_BLOCK_SIZE);
        }

        return true;
    }

    @Override
    public char getMapInfo() {
        return 0;
    }
}
