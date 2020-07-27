package net.simon987.mar.construction;

import net.simon987.mar.server.GameServer;

public class ConstructionPlugin {

    public void init(GameServer gameServer) {
        // TODO
        BluePrintUtil.setSecretKey(gameServer.getSecretKey());
        BluePrintRegistry.INSTANCE.registerBluePrint(ObstacleBlueprint.class);
    }
}
