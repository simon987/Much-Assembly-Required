package net.simon987.biomassplugin.event;

import net.simon987.biomassplugin.BiomassBlob;
import net.simon987.biomassplugin.WorldUtils;
import net.simon987.server.GameServer;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.event.WorldUpdateEvent;
import net.simon987.server.game.world.World;

import java.util.ArrayList;
import java.util.HashMap;


public class WorldUpdateListener implements GameEventListener {

    private HashMap<World, Long> worldWaitMap = new HashMap<>(200);

    private static int minBlobCount;
    private static int maxBlobCount;
    private static int blobYield;
    private static int waitTime;
    private static int blobThreshold;

    public WorldUpdateListener(ServerConfiguration config) {

        minBlobCount = config.getInt("minBiomassRespawnCount");
        maxBlobCount = config.getInt("maxBiomassRespawnCount");
        waitTime = config.getInt("biomassRespawnTime");
        blobThreshold = config.getInt("biomassRespawnThreshold");
        blobYield = config.getInt("biomass_yield");

    }

    @Override
    public Class getListenedEventType() {
        return WorldUpdateEvent.class;
    }


    @Override
    public void handle(GameEvent event) {

        World world = ((WorldUpdateEvent) event).getWorld();

        if (world.getDimension().startsWith("w")) {
            //If there is less than the respawn threshold,
            if (world.findObjects(BiomassBlob.class).size() < blobThreshold) {

                //Set a timer for respawn_time ticks
                if (!worldWaitMap.containsKey(world) || worldWaitMap.get(world) == 0L) {
                    worldWaitMap.put(world, GameServer.INSTANCE.getGameUniverse().getTime() + waitTime);
                } else {

                    long waitUntil = worldWaitMap.get(world);

                    if (GameServer.INSTANCE.getGameUniverse().getTime() >= waitUntil) {

                        //If the timer was set less than respawn_time ticks ago, respawn the blobs
                        ArrayList<BiomassBlob> newBlobs = WorldUtils.generateBlobs(world, minBlobCount,
                                maxBlobCount, blobYield);
                        for (BiomassBlob blob : newBlobs) {
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