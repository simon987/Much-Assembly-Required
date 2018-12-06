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

    /**
     * Set to true when all the requirements are met
     */
    private boolean completed;

    public BluePrint() {
        requiredItems = new HashMap<>();
    }

    public BluePrint(Document document) {
        requiredItems = (Map<Integer, Integer>) document.get("required_items");
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

        if (requiredItems.containsKey(item.getId()) && requiredItems.get(item.getId()) > 0) {
            requiredItems.put(item.getId(), requiredItems.get(item.getId()) - 1);
            checkCompleted();
            return true;
        }
        return false;
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

        json.put("target", targetObject);
        json.put("required_items", requiredItems);

        return json;
    }

    @Override
    public Document mongoSerialise() {
        Document document = new Document();

        document.put("completed", completed);
        document.put("target", targetObject);
        document.put("required_items", requiredItems);

        return document;
    }
}
