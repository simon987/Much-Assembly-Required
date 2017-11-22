package net.simon987.server.event;

/**
 * Listens for and handles a single type of event
 */
public interface GameEventListener {

    Class getListenedEventType();

    void handle(GameEvent event);

}
