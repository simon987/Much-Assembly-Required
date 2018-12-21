package net.simon987.server.game.world;

import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.GameObject;

public class TileFluid extends Tile {

    public static final int ID = 6;

    @Override
    public int getId() {
        return 6;
    }

    @Override
    public Item drill() {
        return null;
    }

    @Override
    public boolean walk(GameObject object) {
        object.setDead(true);
        return false;
    }

    @Override
    public boolean isBlocked() {
        return true;
    }
}
