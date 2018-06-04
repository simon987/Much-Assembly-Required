package net.simon987.server.game.item;

import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.io.JSONSerialisable;
import net.simon987.server.io.MongoSerializable;
import org.bson.Document;
import org.json.simple.JSONObject;

public abstract class Item implements JSONSerialisable, MongoSerializable {


    public Item(Document document) {

    }

    /**
     * Called when polled
     *
     * @return result of poll operation
     */
    public abstract char poll();

    /**
     * Called when a controllableUnit clears this item from inventory
     */
    public void clear(ControllableUnit unit) {

    }

    /**
     * Used to uniquely identify an item type in the database and in the game
     */
    public abstract int getId();

    @Override
    public JSONObject jsonSerialise() {

        JSONObject json = new JSONObject();

        json.put("type", getId());

        return json;
    }

    @Override
    public JSONObject debugJsonSerialise() {
        return jsonSerialise();
    }

    @Override
    public Document mongoSerialise() {
        Document document = new Document();

        document.put("type", getId());

        return document;
    }

}
