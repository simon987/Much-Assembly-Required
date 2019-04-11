package net.simon987.cubotplugin.event;

import net.simon987.cubotplugin.Cubot;
import net.simon987.server.event.GameEvent;

public class CubotWalkEvent extends GameEvent {

    public CubotWalkEvent(Cubot cubot) {
        setSource(cubot);
    }

    @Override
    public Cubot getSource() {
        return (Cubot) super.getSource();
    }

}