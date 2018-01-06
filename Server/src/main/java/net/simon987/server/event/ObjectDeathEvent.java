package net.simon987.server.event;

/**
 * Event dispatched by a GameObject who has needed callbacks on death
 */
public class ObjectDeathEvent extends GameEvent {
    /**
     * The GameObject type ID of object that init this event
     */
    private long sourceObjectId;

    public ObjectDeathEvent(Object source, int sourceObjectId) {
        setSource(source);
        this.sourceObjectId = sourceObjectId;
    }

    public long getSourceObjectId() {
        return sourceObjectId;
    }
}
