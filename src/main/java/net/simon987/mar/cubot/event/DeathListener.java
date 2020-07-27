package net.simon987.mar.cubot.event;

import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import net.simon987.mar.server.game.objects.ControllableUnit;
import net.simon987.mar.server.game.objects.GameObject;

public class DeathListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DeathEvent.class;
    }

    @Override
    public void handle(GameEvent event) {
        DeathEvent DeathEvent = (DeathEvent) event;
        GameObject object = DeathEvent.getSource();
        if (object instanceof ControllableUnit) {
            ((ControllableUnit) object).getParent().getStats().incrementStat("death", 1);
        }
    }
}
