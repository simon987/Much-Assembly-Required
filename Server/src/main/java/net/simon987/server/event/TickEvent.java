package net.simon987.server.event;

public class TickEvent extends GameEvent {

    long time;

    public TickEvent(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
