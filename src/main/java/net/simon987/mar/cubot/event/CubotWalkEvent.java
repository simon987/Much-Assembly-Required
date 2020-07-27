package net.simon987.mar.cubot.event;

import net.simon987.mar.cubot.Cubot;
import net.simon987.mar.server.event.GameEvent;

public class CubotWalkEvent extends GameEvent {

    public CubotWalkEvent(Cubot cubot) {
        setSource(cubot);
    }

    @Override
    public Cubot getSource() {
        return (Cubot) super.getSource();
    }

}
