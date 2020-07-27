package net.simon987.mar.server.game.item;

/**
 * Invalid/empty item
 */
public class ItemVoid extends Item {

    public ItemVoid() {
        super(null);
    }

    @Override
    public char poll() {
        return 0;
    }

    @Override
    public int getId() {
        return 0;
    }
}
