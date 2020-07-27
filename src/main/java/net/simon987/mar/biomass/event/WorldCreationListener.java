package net.simon987.mar.biomass.event;

import net.simon987.mar.biomass.BiomassBlob;
import net.simon987.mar.biomass.WorldUtils;
import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import net.simon987.mar.server.event.WorldGenerationEvent;

import java.util.ArrayList;

public class WorldCreationListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return WorldGenerationEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        int minCount = GameServer.INSTANCE.getConfig().getInt("minBiomassCount");
        int maxCount = GameServer.INSTANCE.getConfig().getInt("maxBiomassCount");
        int yield = GameServer.INSTANCE.getConfig().getInt("biomass_yield");

        ArrayList<BiomassBlob> biomassBlobs = WorldUtils.generateBlobs(((WorldGenerationEvent) event).getWorld(),
                minCount, maxCount, yield);

        for (BiomassBlob blob : biomassBlobs) {
            ((WorldGenerationEvent) event).getWorld().addObject(blob);
        }

    }
}
