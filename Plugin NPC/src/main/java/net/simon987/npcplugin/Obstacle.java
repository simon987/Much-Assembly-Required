package net.simon987.npcplugin;

import net.simon987.server.game.objects.Attackable;
import net.simon987.server.game.objects.GameObject;
import org.bson.Document;
import org.json.simple.JSONObject;

/**
 * Generic game object that blocks the path.
 */
public class Obstacle extends GameObject implements Attackable {

    public static final int MAP_INFO = 0x0701;

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

    public Obstacle(Document document) {
        super(document);
        style = document.getInteger("style");
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
    public Document mongoSerialise() {
        Document dbObject = super.mongoSerialise();

        dbObject.put("hp", hp);
        dbObject.put("style", style);

        return dbObject;
    }

    @Override
    public JSONObject jsonSerialise() {
        JSONObject json = super.jsonSerialise();

        json.put("hp", hp);
        json.put("style", style);

        return json;
    }
}
