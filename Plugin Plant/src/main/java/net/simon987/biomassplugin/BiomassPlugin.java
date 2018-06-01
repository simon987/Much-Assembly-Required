package net.simon987.biomassplugin;

import net.simon987.biomassplugin.event.ObjectDeathListener;
import net.simon987.biomassplugin.event.WorldCreationListener;
import net.simon987.biomassplugin.event.WorldUpdateListener;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.objects.GameRegistry;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;


public class BiomassPlugin extends ServerPlugin {


    @Override
    public void init(ServerConfiguration config, GameRegistry registry) {
        listeners.add(new WorldCreationListener());
        listeners.add(new WorldUpdateListener(config));

        if (registry.isGameObjectRegistered("net.simon987.npcplugin.HarvesterNPC")) {
            listeners.add(new ObjectDeathListener(config));
        } else {
            LogManager.LOGGER.severe("(BiomassPlugin) NPC plugin is not loaded so biomass will not spawn on death of HarvesterNPC");
        }

        registry.registerGameObject(BiomassBlob.class);
        registry.registerItem(ItemBiomass.ID, ItemBiomass.class);

        LogManager.LOGGER.info("(BiomassPlugin) Initialised Biomass plugin");
    }
}
