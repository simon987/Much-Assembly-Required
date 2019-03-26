package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.logging.LogManager;

public class TotalExecutionTimeListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return TotalExecutionTimeEvent.class;
    }

    @Override
    public void handle(GameEvent event) {
        TotalExecutionTimeEvent TotalExecutionTimeEvent = (TotalExecutionTimeEvent) event;
        GameObject object = TotalExecutionTimeEvent.getSource();
        if (object instanceof ControllableUnit) {
            LogManager.LOGGER.info(((ControllableUnit) object).getParent().getUsername() + " Death Count " +
                    Double.toString(object.getTime()));

            ((ControllableUnit) object).getParent().getStats().setDouble("totalExecutionTime",
                    TotalExecutionTimeEvent.getTime());
        }
    }
}