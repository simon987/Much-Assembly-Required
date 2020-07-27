package net.simon987.mar.server.game.world;

import net.simon987.mar.server.game.objects.GameObject;

public class TileWall extends Tile {

    public static final int ID = 1;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public boolean walk(GameObject object) {
        return false; //always blocked
    }

    @Override
    public boolean isBlocked() {
        return true;
    }
}
