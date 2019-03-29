package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.game.objects.GameObject;

public class ExecutionTimeEvent extends GameEvent {

    private int Time = 0;

    public ExecutionTimeEvent(GameObject object, int time) {
        setSource(object);
        this.Time = time;
    }

    public int getTime(){
        return this.Time;
    }

    @Override
    public GameObject getSource() {
        return (GameObject) super.getSource();
    }
}