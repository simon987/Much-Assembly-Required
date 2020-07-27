package net.simon987.mar.server.event;


import java.util.ArrayList;


public class GameEventDispatcher {

    private final ArrayList<GameEventListener> listeners;

    public GameEventDispatcher() {
        listeners = new ArrayList<>();
    }

    public void dispatch(GameEvent event) {

        for (GameEventListener listener : listeners) {
            if (event.getClass().equals(listener.getListenedEventType())) {
                listener.handle(event);
            }
        }
    }

    public ArrayList<GameEventListener> getListeners() {
        return listeners;
    }
}
