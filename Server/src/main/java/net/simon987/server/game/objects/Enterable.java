package net.simon987.server.game.objects;

public interface Enterable {

    /**
     * Called when an object attempts to walk directly into a Enterable object
     *
     * @param object The game object that attempted to enter
     * @return true if successful, false to block the object
     */
    boolean enter(GameObject object);

}
