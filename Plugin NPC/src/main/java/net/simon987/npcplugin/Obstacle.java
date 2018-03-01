package net.simon987.npcplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.game.Attackable;
import net.simon987.server.game.GameObject;
import org.json.simple.JSONObject;

/**
 * Generic game object that blocks the path.
 * Some types of obstacles might have some more interesting features (see ElectricBox)
 */
public class Obstacle extends GameObject implements Attackable {

    public static final int ID = 6;
    public static final int MAP_INFO = 0x0400;

    /**
     * Style of the obstacle. Will tell the client which sprite to display
     */
    private int style = 0;

    /**
     * Current health of the npc
     */
    private int hp;

    /**
     * Maximum health of the npc
     */
    private int maxHp;

    public Obstacle(int hp) {
        this.hp = hp;
        this.maxHp = hp;
    }

    @Override
    public void setHealRate(int hp) {
        //No op
    }

    @Override
    public void heal(int amount) {
        //No op
    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public void setHp(int hp) {
        this.hp = hp;
    }

    @Override
    public int getMaxHp() {
        return maxHp;
    }

    @Override
    public void setMaxHp(int hp) {
        this.maxHp = hp;
        this.hp = hp;
    }

    @Override
    public void damage(int amount) {
        hp -= amount;

        //YOU ARE DEAD
        if (hp <= 0) {
            setDead(true);
        }
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    @Override
    public BasicDBObject mongoSerialise() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("i", getObjectId());
        dbObject.put("x", getX());
        dbObject.put("y", getY());
        dbObject.put("t", ID);
        dbObject.put("hp", hp);
        dbObject.put("style", style);

        return dbObject;
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();

        json.put("i", getObjectId());
        json.put("x", getX());
        json.put("y", getY());
        json.put("t", ID);
        json.put("hp", hp);
        json.put("style", style);

        return json;
    }

    public static Obstacle deserialize(DBObject obj) {

        Obstacle obstacle = new Obstacle((int) obj.get("hp"));
        obstacle.setObjectId((long) obj.get("i"));
        obstacle.setX((int) obj.get("x"));
        obstacle.setY((int) obj.get("y"));
        obstacle.setStyle((int) obj.get("style"));

        return obstacle;
    }
}
