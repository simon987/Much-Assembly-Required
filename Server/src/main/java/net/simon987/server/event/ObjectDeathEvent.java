package net.simon987.server.event;

/**
 * Event dispatched by a GameObject who has needed callbacks on death
 */
public class ObjectDeathEvent extends GameEvent {

    public ObjectDeathEvent(Object source) {
        setSource(source);
    }
}
