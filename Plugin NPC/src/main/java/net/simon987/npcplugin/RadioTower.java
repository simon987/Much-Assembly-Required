package net.simon987.npcplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.Programmable;
import net.simon987.server.game.Updatable;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;

public class RadioTower extends GameObject implements Programmable, Updatable {

    private static final int MAP_INFO = 0x1000;

    public static final int ID = 4;

    public static final int MAX_RANGE = 3; //todo load from config

    private static final int MAX_MESSAGES = 16;

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }


    /**
     * Messages from the current tick
     */
    private ArrayList<char[]> messages = new ArrayList<>(4);

    /**
     * Messages from the last tick
     */
    private ArrayList<char[]> lastMessages = new ArrayList<>(4);

    @Override
    public void update() {
        lastMessages = new ArrayList<>(messages);
        messages.clear();
    }

    @Override
    public boolean sendMessage(char[] message) {

        if (message.length < MAX_MESSAGES) {
            messages.add(message);
            return true;
        } else {
            return false;
        }
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

    @Override
    public BasicDBObject mongoSerialise() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("i", getObjectId());
        dbObject.put("x", getX());
        dbObject.put("y", getY());
        dbObject.put("t", ID);

        return dbObject;
    }

    public static RadioTower deserialize(DBObject obj) {

        RadioTower tower = new RadioTower();
        tower.setObjectId((long) obj.get("i"));
        tower.setX((int) obj.get("x"));
        tower.setY((int) obj.get("y"));

        NpcPlugin.getRadioTowers().add(tower);

        return tower;
    }


    public ArrayList<char[]> getMessages() {
        return lastMessages;
    }

    /**
     * Get the first directly adjacent tile (starting east, going clockwise)
     */
    public Point getAdjacentTile() {

        if (!getWorld().isTileBlocked(getX() + 1, getY())) {
            return new Point(getX() + 1, getY());

        } else if (!getWorld().isTileBlocked(getX(), getY() + 1)) {
            return new Point(getX(), getY() + 1);

        } else if (!getWorld().isTileBlocked(getX() - 1, getY())) {
            return new Point(getX() - 1, getY());

        } else if (!getWorld().isTileBlocked(getX(), getY() - 1)) {
            return new Point(getX(), getY() - 1);
        } else {
            return null;
        }

    }
}
