package net.simon987.npcplugin.event;

import net.simon987.npcplugin.Factory;
import net.simon987.server.GameServer;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.event.WorldGenerationEvent;
import net.simon987.server.game.World;
import net.simon987.server.logging.LogManager;

import java.util.Random;

public class WorldCreationListener implements GameEventListener {

    /**
     * Spawn rate. Higher = rarer: A factory will be spawn about every FACTORY_SPAWN_RATE generated Worlds
     */
    private static final int FACTORY_SPAWN_RATE = 35;

    private Random random = new Random();

    @Override
    public Class getListenedEventType() {
        return WorldGenerationEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        if (random.nextInt(FACTORY_SPAWN_RATE) == 0) {

            World world = ((WorldGenerationEvent) event).getWorld();

            for (int x = 2; x < 12; x++) {
                for (int y = 2; y < 12; y++) {

                    if ((!world.isTileBlocked(x, y) && !world.isTileBlocked(x + 1, y) &&
                            !world.isTileBlocked(x, y + 1) && !world.isTileBlocked(x + 1, y + 1))) {

                        Factory factory = new Factory();

                        factory.setWorld(world);
                        factory.setObjectId(GameServer.INSTANCE.getGameUniverse().getNextObjectId());
                        factory.setX(x);
                        factory.setY(y);

                        if (factory.getAdjacentTile() == null) {
                            //Factory has no non-blocked adjacent tiles
                            continue;
                        }

                        world.getGameObjects().add(factory);
                        world.incUpdatable();

                        LogManager.LOGGER.info("Spawned Factory at (" + world.getX() + ", " + world.getY() +
                                ") (" + x + ", " + y + ")");

                        return;
                    }
                }
            }
        }
    }
}
