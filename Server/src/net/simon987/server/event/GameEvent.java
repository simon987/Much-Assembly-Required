package net.simon987.server.event;


public class GameEvent {

    /**
     * If the event is cancelled the action won't be performed
     */
    private boolean cancelled = false;

    /**
     * The game object that triggered the event
     */
    private Object source;


    //----------------------------
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }
}
