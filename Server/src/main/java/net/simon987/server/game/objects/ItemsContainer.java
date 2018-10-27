package net.simon987.server.game.objects;

import net.simon987.server.game.item.Item;
import org.bson.Document;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ItemsContainer extends GameObject implements InventoryHolder {

    private static final char MAP_INFO = 0x0240;

    private final Map<Integer, Integer> itemsTypeCount;
    private int containerCapacity;
    private int size = 0;

    public ItemsContainer(int containerCapacity) {
        this.containerCapacity = containerCapacity;
        this.itemsTypeCount = new HashMap<>();
    }

    public ItemsContainer(Document document) {
        super(document);
        itemsTypeCount = (Map<Integer, Integer>) document.get("itemsCount");
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public boolean placeItem(Item item) {
        if (size < containerCapacity) {
            int itemId = item.getId();
            itemsTypeCount.putIfAbsent(itemId, 0);
            Integer oldCount = itemsTypeCount.get(itemId);
            itemsTypeCount.replace(itemId, ++oldCount);
            size++;
            return true;
        }
        return false;
    }

    @Override
    public void takeItem(int itemId) {
        Integer oldCount = itemsTypeCount.get(itemId);
        itemsTypeCount.replace(itemId, --oldCount);
        size--;
    }

    @Override
    public boolean canTakeItem(int itemId) {
        Integer integer = itemsTypeCount.get(itemId);
        return integer != null && integer > 0;
    }

    @Override
    public JSONObject jsonSerialise() {
        JSONObject json = super.jsonSerialise();
        json.put("itemsCount", itemsTypeCount);
        return json;
    }

    @Override
    public Document mongoSerialise() {
        Document dbObject = super.mongoSerialise();
        dbObject.put("itemsCount", itemsTypeCount);
        return dbObject;
    }
}
