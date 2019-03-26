package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.game.objects.GameObject;

public class WalkDistanceEvent extends GameEvent {

    public WalkDistanceEvent(GameObject object) {
        setSource(object);
    }

    @Override
    public GameObject getSource() {
        return (GameObject) super.getSource();
    }

}