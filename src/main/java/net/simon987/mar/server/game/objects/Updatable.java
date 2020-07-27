package net.simon987.mar.server.game.objects;

/**
 * Updatable objects needs to be updated each tick
 */
public interface Updatable {

    /**
     * Called every tick
     */
    void update();

}
