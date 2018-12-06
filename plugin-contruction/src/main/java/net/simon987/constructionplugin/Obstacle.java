package net.simon987.constructionplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.objects.Attackable;
import net.simon987.server.game.objects.Structure;
import net.simon987.server.game.objects.Updatable;
import org.bson.Document;
import org.json.simple.JSONObject;

public class Obstacle extends Structure implements Attackable, Updatable {

    public static final int MAP_INFO = 0x0601;
    private static final int HEAL_RATE = GameServer.INSTANCE.getConfig().getInt("obstacle_regen");
    private static final int MAX_HP = GameServer.INSTANCE.getConfig().getInt("obstacle_hp");

    private int hp;
    private int color;

    public Obstacle() {
        super(1, 1);
    }

    public Obstacle(Document document) {
        super(document, 1, 1);

        hp = document.getInteger(hp);
        color = document.getInteger(color);
    }

    @Override
    public void update() {
        heal(HEAL_RATE);
    }

    @Override
    public void setHealRate(int hp) {

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
        return MAX_HP;
    }

    @Override
    public void setMaxHp(int hp) {

    }

    @Override
    public void heal(int amount) {
        hp = Math.min(getMaxHp(), hp + amount);
    }


    @Override
    public void damage(int amount) {
        hp -= amount;

        if (hp < 0) {
            setDead(true);
        }
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public Document mongoSerialise() {
        Document document = super.mongoSerialise();

        document.put("hp", hp);
        document.put("color", hp);

        return document;
    }

    @Override
    public JSONObject debugJsonSerialise() {
        return jsonSerialise();
    }

    @Override
    public JSONObject jsonSerialise() {
        JSONObject json = super.jsonSerialise();

        json.put("hp", hp);
        json.put("color", hp);

        return json;
    }
}
