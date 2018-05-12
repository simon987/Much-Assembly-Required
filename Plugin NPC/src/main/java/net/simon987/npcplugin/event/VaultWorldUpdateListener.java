package net.simon987.npcplugin.event;

import net.simon987.npcplugin.ElectricBox;
import net.simon987.npcplugin.VaultWorldUtils;
import net.simon987.server.GameServer;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.event.WorldUpdateEvent;
import net.simon987.server.game.world.World;

import java.util.ArrayList;
import java.util.HashMap;

public class VaultWorldUpdateListener implements GameEventListener {

    /**
     * Map of worlds and their time to wait until next respawn event
     */
    private HashMap<World, Long> worldWaitMap = new HashMap<>(200);

    /**
     * Lower bound of ElectricBox to be created on a respawn event
     */
    private static int minElectricBoxCount;
    /**
     * Upper bound of ElectricBox to be created on a respawn event
     */
    private static int maxElectricBoxCount;
    /**
     * Number of game ticks to wait after the threshold has been met
     */
    private static int waitTime;
    /**
     * Threshold before starting the
     */
    private static int electricBoxThreshold;

    public VaultWorldUpdateListener(ServerConfiguration config) {

        minElectricBoxCount = config.getInt("min_electric_box_respawn_count");
        maxElectricBoxCount = config.getInt("max_electric_box_respawn_count");
        waitTime = config.getInt("electric_box_respawnTime");
        electricBoxThreshold = config.getInt("min_electric_box_count");
    }

    @Override
    public Class getListenedEventType() {
        return WorldUpdateEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        //TODO: Move this and the Biomass UpdateListener to a 'RespawnManager' kind of deal
        World world = ((WorldUpdateEvent) event).getWorld();

        if (world.getDimension().startsWith("v")) {
            //If there is less than the respawn threshold,
            if (world.findObjects(ElectricBox.class).size() < electricBoxThreshold) {

                //Set a timer for respawn_time ticks
                if (!worldWaitMap.containsKey(world) || worldWaitMap.get(world) == 0L) {
                    worldWaitMap.put(world, GameServer.INSTANCE.getGameUniverse().getTime() + waitTime);
                } else {

                    long waitUntil = worldWaitMap.get(world);

                    if (GameServer.INSTANCE.getGameUniverse().getTime() >= waitUntil) {

                        //If the timer was set less than respawn_time ticks ago, respawn the blobs
                        ArrayList<ElectricBox> newBoxes = VaultWorldUtils.generateElectricBoxes(world, minElectricBoxCount,
                                maxElectricBoxCount);
                        for (ElectricBox blob : newBoxes) {
                            world.addObject(blob);
                            world.incUpdatable();
                        }

                        //Set the 'waitUntil' time to 0 to indicate that we are not waiting
                        worldWaitMap.replace(world, 0L);
                    }
                }
            }
        }
    }
}
