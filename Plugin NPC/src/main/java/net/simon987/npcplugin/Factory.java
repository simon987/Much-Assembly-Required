package net.simon987.npcplugin;

import net.simon987.server.game.GameObject;
import net.simon987.server.game.Updatable;
import org.json.simple.JSONObject;

public class Factory extends GameObject implements Updatable {

    private static final int MAP_INFO = 0x0200;

    public static final int ID = 3;

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public void update() {
        System.out.println("Updating Factory...");
    }

    @Override
    public boolean isAt(int x, int y) {

        /*
         * Object is 2x2 tiles, the (x,y) coordinates of the object being
         * at top-left.
         * # .
         * . .
         */
        return (x == getX() + 1 || x == getX()) && (y == getY() + 1 || y == getY());
    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();

        json.put("i", getObjectId());
        json.put("x", getX());
        json.put("y", getY());
        json.put("t", ID);

        return json;
    }

    public static Factory deserialise(JSONObject json) {

        Factory factory = new Factory();
        factory.setObjectId((int) (long) json.get("i"));
        factory.setX((int) (long) json.get("x"));
        factory.setY((int) (long) json.get("y"));

        return factory;
    }
}
