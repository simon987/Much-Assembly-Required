package net.simon987.server.game.world;

import net.simon987.server.game.item.Item;
import net.simon987.server.game.item.ItemIron;

public class TileIron extends Tile {

    public static final int ID = 2;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public Item drill() {
        return new ItemIron();
    }
}
