package net.simon987.pluginradioactivecloud.event;

import net.simon987.pluginradioactivecloud.RadioactiveObstacle;
import net.simon987.pluginradioactivecloud.RadioactiveWorldUtils;
import net.simon987.server.GameServer;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.event.WorldGenerationEvent;

import java.util.ArrayList;

public class WorldCreationListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return WorldGenerationEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        int minCount = GameServer.INSTANCE.getConfig().getInt("min_radioactive_obstacle_count");
        int maxCount = GameServer.INSTANCE.getConfig().getInt("max_radioactive_obstacle_count");

        ArrayList<RadioactiveObstacle> radioactiveObstacles = RadioactiveWorldUtils
                .generateRadioactiveObstacles(((WorldGenerationEvent) event).getWorld(), minCount, maxCount);

        for (RadioactiveObstacle radioactiveObstacle : radioactiveObstacles) {
            ((WorldGenerationEvent) event).getWorld().addObject(radioactiveObstacle);
        }

    }
}
