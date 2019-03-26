package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.game.objects.GameObject;

public class DeathCountEvent extends GameEvent {

    public DeathCountEvent(GameObject object) {
        setSource(object);
        
    }

    @Override
    public GameObject getSource() {
        return (GameObject) super.getSource();
    }
}