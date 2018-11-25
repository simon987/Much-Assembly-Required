package net.simon987.server.game.world;

import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.GameObject;

public abstract class Tile {

    /**
     * @return Unique id of the tile
     */
    public abstract int getId();

    /**
     * Called when an object attempts to drill this tile
     *
     * @return The item obtained by drilling, return null for no item
     */
    public Item drill() {
        return null;
    }

    /**
     * Called when a player attempts to walk on this tile
     *
     * @return true if the object can walk on this tile, false if blocked
     */
    public boolean walk(GameObject object) {
        return true;
    }

    /**
     * Check if a tile should be considered 'blocked' (by the LiDAR, for example)
     */
    public boolean isBlocked() {
        return false;
    }
}
