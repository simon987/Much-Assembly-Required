package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.logging.LogManager;

public class DeathCountListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return DeathCountEvent.class;
    }

    @Override
    public void handle(GameEvent event) {
        DeathCountEvent DeathCountEvent = (DeathCountEvent) event;
        GameObject object = DeathCountEvent.getSource();
        if (object instanceof ControllableUnit) {
            LogManager.LOGGER.info(((ControllableUnit) object).getParent().getUsername() + " Death Count " +
                    Integer.toString(object.getCounter()));

            ((ControllableUnit) object).getParent().getStats().setInt("deathCount",
                    DeathCountEvent.getCounter());
        }
    }
}