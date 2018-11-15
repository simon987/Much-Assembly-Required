package net.simon987.server.game.world;

import net.simon987.server.game.item.Item;
import net.simon987.server.game.item.ItemCopper;

public class TileCopper extends Tile {

    public static final int ID = 3;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public Item drill() {
        return new ItemCopper();
    }
}
