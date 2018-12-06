package net.simon987.constructionplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.objects.GameRegistry;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;

public class ConstructionPlugin extends ServerPlugin {

    @Override
    public void init(GameServer gameServer) {

        BluePrintUtil.setSecretKey(gameServer.getSecretKey());
        GameRegistry gameRegistry = gameServer.getRegistry();

        gameRegistry.registerItem(ItemBluePrint.ID, ItemBluePrint.class);
        gameRegistry.registerGameObject(Obstacle.class);
        gameRegistry.registerGameObject(ConstructionSite.class);

        BluePrintRegistry.INSTANCE.registerBluePrint(ObstacleBlueprint.class);

        LogManager.LOGGER.info("(Construction Plugin) Initialized construction plugin");
    }
}
