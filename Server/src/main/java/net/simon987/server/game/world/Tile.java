package net.simon987.server.game.world;

import net.simon987.server.game.item.Item;

public abstract class Tile {

    protected boolean blocked;

    /**
     * @return Unique id of the tile
     */
    public abstract int getId();

    public Item onDrill() {
        return null;
    }

    public boolean isBlocked() {
        return blocked;
    }
}
