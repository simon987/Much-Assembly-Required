package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.UserStats;

public class WalkListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return WalkEvent.class;
    }

    @Override
    public void handle(GameEvent event) {
        WalkEvent WalkEvent = (WalkEvent) event;
        GameObject object = WalkEvent.getSource();
        if (object instanceof ControllableUnit) {
            ((ControllableUnit) object).getParent().getStats().incrementStat("walkDistance",
                    1);
        }
    }
}