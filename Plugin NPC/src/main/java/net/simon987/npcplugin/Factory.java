package net.simon987.npcplugin;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.Updatable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;

public class Factory extends GameObject implements Updatable {

    private static final int MAP_INFO = 0x0200;
    static final int ID = 3;

    private static final int MAX_NPC_COUNT = GameServer.INSTANCE.getConfig().getInt("factory_max_npc_count");

    private static final int NPC_CREATION_COOLDOWN = NonPlayerCharacter.LIFETIME / MAX_NPC_COUNT;

    private ArrayList<NonPlayerCharacter> npcs = new ArrayList<>();

    /**
     * Number of ticks to wait until the Factory can spawn a new NPC
     */
    private int cooldown = 0;

    /**
     * Temporary NPC objectId array. The Factory links the NPCs to itself when initialised,
     * at the first call of update().
     */
    private Object[] tmpNpcArray = new Object[0];

    /**
     * Factory are uninitialised until the first update() call
     */
    private boolean initialised = false;

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public void update() {

        if (!initialised) {

            initialised = true;

            for (Object id : tmpNpcArray) {

                NonPlayerCharacter npc = (NonPlayerCharacter) GameServer.INSTANCE.getGameUniverse().getObject((int) (long) id);

                if (npc != null) {
                    npc.setFactory(this);
                    npcs.add(npc);
                }

            }

        } else {

            if (cooldown == 0) {
                if (npcs.size() < MAX_NPC_COUNT) {
                    Point p = getAdjacentTile();

                    if (p != null) {
                        NonPlayerCharacter npc = new HarvesterNPC();
                        npc.setWorld(getWorld());
                        npc.setObjectId(GameServer.INSTANCE.getGameUniverse().getNextObjectId());
                        npc.setX(p.x);
                        npc.setY(p.y);
                        getWorld().addObject(npc);
                        getWorld().incUpdatable();
                        npc.setFactory(this);

                        npcs.add(npc);
                    }
                }

                cooldown += NPC_CREATION_COOLDOWN;

            } else {
                cooldown--;
            }
        }
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

        JSONArray tmpNpcArray = new JSONArray();

        for (NonPlayerCharacter npc : npcs) {
            tmpNpcArray.add(npc.getObjectId());
        }

        json.put("n", tmpNpcArray);

        return json;
    }

    @Override
    public BasicDBObject mongoSerialise() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("i", getObjectId());
        dbObject.put("x", getX());
        dbObject.put("y", getY());
        dbObject.put("t", ID);

        BasicDBList tmpNpcArray = new BasicDBList();

        for (NonPlayerCharacter npc : npcs) {
            tmpNpcArray.add(npc.getObjectId());
        }

        dbObject.put("n", tmpNpcArray);

        return dbObject;
    }

    public static Factory deserialise(DBObject obj) {

        Factory factory = new Factory();
        factory.setObjectId((long) obj.get("i"));
        factory.setX((int) obj.get("x"));
        factory.setY((int) obj.get("y"));

        factory.tmpNpcArray = ((BasicDBList) obj.get("n")).toArray();

        return factory;
    }

    /**
     * Get the first non-blocked tile that is directly adjacent to the factory, starting from the north-east corner
     * going clockwise.
     *
     * @return The coordinates of the first non-blocked tile, null otherwise.
     */
    public Point getAdjacentTile() {

        /*
         * (2,0)
         * (2,1)
         * (1,2)
         * (0,2)
         * (-1,1)
         * (-1,0)
         * (0,-1)
         * (1,-1)
         */

        if (!getWorld().isTileBlocked(getX() + 2, getY())) {
            return new Point(getX() + 2, getY());

        } else if (!getWorld().isTileBlocked(getX() + 2, getY() + 1)) {
            return new Point(getX() + 2, getY() + 1);

        } else if (!getWorld().isTileBlocked(getX() + 1, getY() + 2)) {
            return new Point(getX() + 1, getY() + 2);

        } else if (!getWorld().isTileBlocked(getX(), getY() + 2)) {
            return new Point(getX(), getY() + 2);

        } else if (!getWorld().isTileBlocked(getX() + -1, getY() + 1)) {
            return new Point(getX() + -1, getY() + 1);

        } else if (!getWorld().isTileBlocked(getX() + -1, getY())) {
            return new Point(getX() + -1, getY());

        } else if (!getWorld().isTileBlocked(getX(), getY() + -1)) {
            return new Point(getX(), getY() + -1);

        } else if (!getWorld().isTileBlocked(getX() + 1, getY() + -1)) {
            return new Point(getX() + 1, getY() + -1);

        } else {
            return null;
        }

    }

    ArrayList<NonPlayerCharacter> getNpcs() {
        return npcs;
    }
}
