package net.simon987.npcplugin.world;

import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.world.Tile;

public class TileVaultWall extends Tile {

    public static final int ID = 5;

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
