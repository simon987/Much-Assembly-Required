package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;

public class WalkListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return CubotWalkEvent.class;
    }

    @Override
    public void handle(GameEvent event) {
        CubotWalkEvent walkEvent = (CubotWalkEvent) event;
        walkEvent.getSource().getParent().getStats().incrementStat("walkDistance", 1);
    }
}