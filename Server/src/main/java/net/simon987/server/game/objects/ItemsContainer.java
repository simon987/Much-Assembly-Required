package net.simon987.server.game.objects;

import net.simon987.server.game.item.Item;
import org.bson.Document;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemsContainer extends GameObject implements InventoryHolder {

    private static final char MAP_INFO = 0x0601;

    private final List<Item> items;
    private int containerCapacity;

    public ItemsContainer(int containerCapacity) {
        this.containerCapacity = containerCapacity;
        this.items = new ArrayList<>();
    }

    public ItemsContainer(Document document) {
        super(document);
        this.items = (List<Item>) document.get("items");
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public boolean placeItem(Item item) {
        if (items.size() < containerCapacity) {
            items.add(item);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void takeItem(int itemId) {
        Optional<Item> first = items.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst();

        items.remove(first.get());
    }

    @Override
    public boolean canTakeItem(int itemId) {
        return items.stream()
                .anyMatch(item -> item.getId() == itemId);
    }

    @Override
    public JSONObject jsonSerialise() {
        JSONObject json = super.jsonSerialise();
        json.put("items", items);
        return json;
    }

    @Override
    public Document mongoSerialise() {
        Document dbObject = super.mongoSerialise();
        dbObject.put("items", items);
        return dbObject;
    }
}
