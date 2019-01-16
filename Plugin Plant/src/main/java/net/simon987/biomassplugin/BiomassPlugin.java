package net.simon987.biomassplugin;

import net.simon987.biomassplugin.event.ObjectDeathListener;
import net.simon987.biomassplugin.event.WorldCreationListener;
import net.simon987.biomassplugin.event.WorldUpdateListener;
import net.simon987.server.GameServer;
import net.simon987.server.IServerConfiguration;
import net.simon987.server.game.objects.GameRegistry;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;


public class BiomassPlugin extends ServerPlugin {


    @Override
    public void init(GameServer gameServer) {

        IServerConfiguration config = gameServer.getConfig();
        GameRegistry registry = gameServer.getRegistry();

        listeners.add(new WorldCreationListener());
        listeners.add(new WorldUpdateListener(config));
        listeners.add(new ObjectDeathListener(config));

        registry.registerGameObject(BiomassBlob.class);
        registry.registerItem(ItemBiomass.ID, ItemBiomass.class);

        LogManager.LOGGER.info("(BiomassPlugin) Initialised Biomass plugin");
    }
}
