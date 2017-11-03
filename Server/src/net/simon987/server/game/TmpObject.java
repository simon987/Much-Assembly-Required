package net.simon987.server.game;

import net.simon987.server.GameServer;

public class TmpObject extends GameObject{

    public TmpObject(){

        GameServer.INSTANCE.getGameUniverse();

        setWorld(GameServer.INSTANCE.getGameUniverse().getWorld(0,0));
        setX(6);
        setY(6);

    }

    @Override
    public char getMapInfo() {
        return 0;
    }
}
