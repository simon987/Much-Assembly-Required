package net.simon987.server.game;

/**
 * Updatable objects needs to be updated each tick
 */
public interface Updatable {

    /**
     * Called every tick
     */
    void update();

}
