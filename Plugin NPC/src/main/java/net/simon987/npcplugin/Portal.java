package net.simon987.npcplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.game.*;
import org.json.simple.JSONObject;

public class Portal extends GameObject implements Enterable {

    private Location dst;

    public static final int MAP_INFO = 0x0020;

    public static final int ID = 8;

    @Override
    public boolean enter(GameObject object) {

        World world = GameServer.INSTANCE.getGameUniverse().getWorld(dst.worldX, dst.worldY, false, dst.dimension);

        if (object instanceof Updatable) {
            object.getWorld().decUpdatable();
            world.incUpdatable();
        }
        object.getWorld().removeObject(object);
        object.setWorld(world);
        world.addObject(object);

        object.setX(dst.x);
        object.setY(dst.y);

        return true;
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public BasicDBObject mongoSerialise() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("i", getObjectId());
        dbObject.put("x", getX());
        dbObject.put("y", getY());
        dbObject.put("t", ID);
        dbObject.put("dstWorldX", dst.worldX);
        dbObject.put("dstWorldY", dst.worldY);
        dbObject.put("dstX", dst.x);
        dbObject.put("dstY", dst.y);
        dbObject.put("dstDimension", dst.dimension);

        return dbObject;
    }

    public static Portal deserialize(DBObject obj) {

        Portal portal = new Portal();

        portal.dst = new Location(
                (int) obj.get("dstWorldX"),
                (int) obj.get("dstWorldY"),
                (String) obj.get("dstDimension"),
                (int) obj.get("dstX"),
                (int) obj.get("dstY"));
        portal.setX((int) obj.get("x"));
        portal.setY((int) obj.get("y"));

        return portal;
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

    public Location getDst() {
        return dst;
    }

    public void setDst(Location dst) {
        this.dst = dst;
    }
}
