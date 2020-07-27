package net.simon987.mar.npcplugin.event;

import net.simon987.mar.npcplugin.NpcPlugin;
import net.simon987.mar.npcplugin.Settlement;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import net.simon987.mar.server.event.WorldGenerationEvent;
import net.simon987.mar.server.game.world.World;
import net.simon987.mar.server.game.world.WorldGenerationException;
import net.simon987.mar.server.logging.LogManager;

import java.util.Random;

public class WorldCreationListener implements GameEventListener {

    /**
     * Spawn rate. Higher = rarer: A factory will be spawn about every FACTORY_SPAWN_RATE generated Worlds
     */
    private static int FACTORY_SPAWN_RATE = 0;

    private final Random random = new Random();

    public WorldCreationListener(int factorySpawnRate) {
        FACTORY_SPAWN_RATE = factorySpawnRate;
    }

    @Override
    public Class getListenedEventType() {
        return WorldGenerationEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        if (random.nextInt(FACTORY_SPAWN_RATE) == 0) {

            World world = (World) event.getSource();

            try {
                Settlement settlement = new Settlement(world);
                NpcPlugin.settlementMap.put(world.getId(), settlement);
            } catch (WorldGenerationException e) {
                LogManager.LOGGER.fine(String.format("Exception during settlement generation: %s.",
                        e.getMessage()));
            }
        }
    }
}
