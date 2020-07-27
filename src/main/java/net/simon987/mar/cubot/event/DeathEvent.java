package net.simon987.mar.cubot.event;

import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.game.objects.GameObject;

public class DeathEvent extends GameEvent {

    public DeathEvent(GameObject object) {
        setSource(object);
    }

    @Override
    public GameObject getSource() {
        return (GameObject) super.getSource();
    }
}
