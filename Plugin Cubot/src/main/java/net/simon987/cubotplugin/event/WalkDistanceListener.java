package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.UserStats;

public class WalkDistanceListener implements GameEventListener {

    private int count;

    @Override
    public Class getListenedEventType() {
        return WalkDistanceEvent.class;
    }

    @Override
    public void handle(GameEvent event) {
        WalkDistanceEvent WalkDistanceEvent = (WalkDistanceEvent) event;
        GameObject object = WalkDistanceEvent.getSource();
        if (object instanceof ControllableUnit) {
            count = ((ControllableUnit) object).getParent().getStats().getInt("walkDistance");
            count++;
            LogManager.LOGGER.info(((ControllableUnit) object).getParent().getUsername() + " walk distance " +
                    count);

            ((ControllableUnit) object).getParent().getStats().setInt("walkDistance",
                    count);
        }
    }
}