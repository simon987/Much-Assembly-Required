package net.simon987.npcplugin;

import net.simon987.npcplugin.event.CpuInitialisationListener;
import net.simon987.npcplugin.event.VaultWorldUpdateListener;
import net.simon987.npcplugin.event.WorldCreationListener;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.objects.GameRegistry;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;

import java.util.ArrayList;

public class NpcPlugin extends ServerPlugin {

    /**
     * Radio tower cache
     */
    private static ArrayList<RadioTower> radioTowers;

    @Override
    public void init(ServerConfiguration configuration, GameRegistry registry) {

        listeners.add(new WorldCreationListener());
        listeners.add(new CpuInitialisationListener());
        listeners.add(new VaultWorldUpdateListener(configuration));

        registry.registerGameObject(HarvesterNPC.class);
        registry.registerGameObject(Factory.class);
        registry.registerGameObject(RadioTower.class);
        registry.registerGameObject(VaultDoor.class);
        registry.registerGameObject(Obstacle.class);
        registry.registerGameObject(ElectricBox.class);
        registry.registerGameObject(Portal.class);
        registry.registerGameObject(VaultExitPortal.class);

        registry.registerHardware(RadioReceiverHardware.class);

        radioTowers = new ArrayList<>(32);

        LogManager.LOGGER.info("Initialised NPC plugin");
    }

    public static ArrayList<RadioTower> getRadioTowers() {
        return radioTowers;
    }

}
