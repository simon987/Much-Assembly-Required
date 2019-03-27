package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.UserStats;

public class WalkDistanceListener implements GameEventListener {

    private int count = 0;
    private int deaths = 0;

    @Override
    public Class getListenedEventType() {
        return WalkDistanceEvent.class;
    }

    @Override
    public void handle(GameEvent event) {
        WalkDistanceEvent WalkDistanceEvent = (WalkDistanceEvent) event;
        GameObject object = WalkDistanceEvent.getSource();
        if (object instanceof ControllableUnit) {
            //When cubot dies walk counter resets
            if(deaths<((ControllableUnit) object).getParent().getStats().getInt("deathCount")){
                count = 0;
                deaths = ((ControllableUnit) object).getParent().getStats().getInt("deathCount");
            }
            count++;
            LogManager.LOGGER.info(((ControllableUnit) object).getParent().getUsername() + " walk distance " +
                    count);
            //Walk distance is only saved if higher than current hightest distance for user
            if(count>((ControllableUnit) object).getParent().getStats().getInt("walkDistance"))
                ((ControllableUnit) object).getParent().getStats().setInt("walkDistance",
                        count);
        }
    }
}