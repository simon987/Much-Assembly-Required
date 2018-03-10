package net.simon987.npcplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.assembly.Util;
import net.simon987.server.game.Attackable;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.Rechargeable;
import net.simon987.server.game.Updatable;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class ElectricBox extends GameObject implements Updatable, Attackable {

    public static final int ID = 7;

    private static final int maxHp = GameServer.INSTANCE.getConfig().getInt("electric_box_hp");
    private static final int damageDealt = GameServer.INSTANCE.getConfig().getInt("electric_box_damage");
    private static final int energyGiven = GameServer.INSTANCE.getConfig().getInt("electric_box_energy_given");

    private int hp;

    private ArrayList<Attackable> nearObjects = new ArrayList<>();

    public ElectricBox() {

        this.hp = maxHp;

    }

    @Override
    public void setHealRate(int hp) {
        //no op
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
        return hp;
    }

    @Override
    public void setMaxHp(int hp) {
        //No op
    }

    @Override
    public void heal(int amount) {
        //No op
    }

    @Override
    public void damage(int amount) {
        hp -= amount;

        //YOU ARE DEAD
        if (hp <= 0) {
            setDead(true);
            LogManager.LOGGER.severe("BOX DEAD");
        }
    }

    @Override
    public char getMapInfo() {
        return Obstacle.MAP_INFO;
    }

    private void updateNearObjects() {

        nearObjects.clear();

        for (GameObject object : getWorld().getGameObjects()) {
            if (object != this && object instanceof Attackable && Util.manhattanDist(object.getX(), object.getY(),
                    getX(), getY()) <= 1) {
                nearObjects.add((Attackable) object);
            }
        }
    }

    @Override
    public void update() {

        updateNearObjects();

        for (Attackable obj : nearObjects) {
            obj.damage(damageDealt);

            if (obj instanceof Rechargeable) {
                ((Rechargeable) obj).storeEnergy(energyGiven);
            }
        }

    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();

        json.put("i", getObjectId());
        json.put("x", getX());
        json.put("y", getY());
        json.put("t", ID);
        json.put("hp", hp);

        return json;
    }

    @Override
    public BasicDBObject mongoSerialise() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("i", getObjectId());
        dbObject.put("x", getX());
        dbObject.put("y", getY());
        dbObject.put("t", ID);
        dbObject.put("hp", getHp());

        return dbObject;
    }

    public static ElectricBox deserialize(DBObject obj) {

        ElectricBox electricBox = new ElectricBox();
        electricBox.setHp((int) obj.get("hp"));
        electricBox.setObjectId((long) obj.get("i"));
        electricBox.setX((int) obj.get("x"));
        electricBox.setY((int) obj.get("y"));

        return electricBox;
    }

    @Override
    public boolean onDeadCallback() {
        getWorld().decUpdatable();
        return false;
    }
}
