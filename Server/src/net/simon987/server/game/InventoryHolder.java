package net.simon987.server.game;


public interface InventoryHolder {

    /**
     * Place an item into the inventory
     *
     * @param item item id (see MarConstants.ITEM_*)
     */
    boolean placeItem(int item);

    /**
     * Take an item from the inventory
     * @param item Desired item id (see MarConstants.ITEM_*)
     * @return true is the take item action executed properly, true also means that the desired item
     * was removed from the inventory
     */
    boolean takeItem(int item);
}
