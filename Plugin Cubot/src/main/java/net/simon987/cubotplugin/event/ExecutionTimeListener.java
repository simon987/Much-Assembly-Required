package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.logging.LogManager;

public class ExecutionTimeListener implements GameEventListener {

    private int count = 0;

    @Override
    public Class getListenedEventType() {
        return ExecutionTimeEvent.class;
    }

    @Override
    public void handle(GameEvent event) {
        ExecutionTimeEvent executionTimeEvent = (ExecutionTimeEvent) event;
        GameObject object = executionTimeEvent.getSource();
        if (object instanceof ControllableUnit) {
            count = ((ControllableUnit) object).getParent().getStats().getInt("executionTime");
            count++;

            ((ControllableUnit) object).getParent().getStats().setInt("executionTime",
                    count);
        }
    }
}