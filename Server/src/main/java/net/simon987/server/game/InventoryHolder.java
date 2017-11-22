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
     *
     * @param item Desired item id (see MarConstants.ITEM_*)
     */
    void takeItem(int item);

    /**
     * @param item item to take
     * @return true if the InventoryHolder can provide this item
     */
    boolean canTakeItem(int item);
}
