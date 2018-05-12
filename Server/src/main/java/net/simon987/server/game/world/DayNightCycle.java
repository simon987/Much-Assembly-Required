package net.simon987.server.game.world;

import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.event.TickEvent;

public class DayNightCycle implements GameEventListener {

    /**
     * Length of an hour in ticks
     */
    private static final int HOUR_LENGTH = 16;

    //Current time of the day (0-23)
    private int currentDayTime;

    //Current light intensity (0-10)
    private int sunIntensity;

    @Override
    public Class getListenedEventType() {
        return TickEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        currentDayTime = (int) ((TickEvent) event).getTime() / HOUR_LENGTH % 24;

        // -0.25x² + 6x - 27 with a minimum of 1
        sunIntensity = Math.max((int) Math.round((-(0.25 * currentDayTime * currentDayTime) + (6 * currentDayTime) - 27)), 0) + 1;
    }

    public int getCurrentDayTime() {
        return currentDayTime;
    }

    public int getSunIntensity() {
        return sunIntensity;
    }
}
