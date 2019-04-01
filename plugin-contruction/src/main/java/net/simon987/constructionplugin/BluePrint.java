package net.simon987.constructionplugin;

import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.InventoryHolder;
import net.simon987.server.io.JSONSerializable;
import net.simon987.server.io.MongoSerializable;
import org.bson.Document;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class BluePrint implements InventoryHolder, JSONSerializable, MongoSerializable {

    /**
     * Map of items id and required amount
     * <br>The amount is decremented as the items are added
     */
    protected Map<Integer, Integer> requiredItems;

    /**
     * This object will be instantiated when completed
     */
    protected Class<? extends GameObject> targetObject;

    static final int DATA_LENGTH = 1024;

    /**
     * Set to true when all the requirements are met
     */
    private boolean completed;

    BluePrint() {
        requiredItems = new HashMap<>();
    }

    public BluePrint(Document document) {
        Map<String, Integer> bsonCompatibleRequiredItems = (Map<String, Integer>) document.get("required_items");
        requiredItems = new HashMap<>(bsonCompatibleRequiredItems.size());

        for (String key : bsonCompatibleRequiredItems.keySet()) {
            requiredItems.put(Integer.valueOf(key), bsonCompatibleRequiredItems.get(key));
        }

        completed = document.getBoolean("completed");
        try {
            targetObject = Class.forName(document.getString("target")).asSubclass(GameObject.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkCompleted() {

        for (Integer remaining : requiredItems.values()) {
            if (remaining > 0) {
                return;
            }
        }

        completed = true;
    }

    @Override
    public boolean placeItem(Item item) {

        if (canPlaceItem(item.getId())) {
            requiredItems.put(item.getId(), requiredItems.get(item.getId()) - 1);
            checkCompleted();
            return true;
        }
        return false;
    }

    @Override
    public boolean canPlaceItem(int itemId) {
        return requiredItems.containsKey(itemId) && requiredItems.get(itemId) > 0;
    }

    @Override
    public void takeItem(int itemId) {

    }

    @Override
    public boolean canTakeItem(int itemId) {
        return false;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Class<? extends GameObject> getTargetObject() {
        return targetObject;
    }

    @Override
    public JSONObject debugJsonSerialise() {
        return jsonSerialise();
    }

    @Override
    public JSONObject jsonSerialise() {
        JSONObject json = new JSONObject();

        json.put("target", targetObject.getName());
        json.put("required_items", requiredItems);

        return json;
    }

    @Override
    public Document mongoSerialise() {
        Document document = new Document();

        document.put("completed", completed);
        document.put("target", targetObject.getName());
        document.put("type", this.getClass().getName());

        Map<String, Integer> bsonCompatibleRequiredItems = new HashMap<>();
        for (Integer key : requiredItems.keySet()) {
            bsonCompatibleRequiredItems.put(String.valueOf(key), requiredItems.get(key));
        }

        document.put("required_items", bsonCompatibleRequiredItems);

        return document;
    }
}
