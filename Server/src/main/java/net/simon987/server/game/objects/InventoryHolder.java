package net.simon987.server.game.objects;


import net.simon987.server.game.item.Item;

public interface InventoryHolder {

    /**
     * Place an item into the inventory
     */
    boolean placeItem(Item item);

    /**
     * Take an item from the inventory
     */
    void takeItem(int itemId);

    /**
     * @param itemId id of the item
     * @return true if the InventoryHolder can provide this item
     */
    boolean canTakeItem(int itemId);
}
