package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.game.objects.GameObject;

public class TotalExecutionTimeEvent extends GameEvent {

    public TotalExecutionTimeEvent(){

    }

    public TotalExecutionTimeEvent(GameObject object, double c) {
        setSource(object);
        if(c>=0){
            object.setTime(c);
        }else{
            object.setTime(0);
        }
    }

    @Override
    public GameObject getSource() {
        return (GameObject) super.getSource();
    }

    public double getTime() {
        return (int) getSource().getTime();
    }
}