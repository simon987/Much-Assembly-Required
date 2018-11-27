package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.Util;
import net.simon987.server.game.objects.Attackable;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.Rechargeable;
import net.simon987.server.game.objects.Updatable;
import org.bson.Document;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Game object that deals damage to nearby objects and gives them energy
 */
public class ElectricBox extends GameObject implements Updatable, Attackable {

    private static final char MAP_INFO = 0x0301;
    /**
     * Hit points
     */
    private int hp;
    /**
     * Maximum hit points
     */
    private static final int maxHp = GameServer.INSTANCE.getConfig().getInt("electric_box_hp");
    /**
     * Number of hit points dealt to nearby objects each tick
     */
    private static final int damageDealt = GameServer.INSTANCE.getConfig().getInt("electric_box_damage");
    /**
     * Number of energy points given to nearby objects each tick
     */
    private static final int energyGiven = GameServer.INSTANCE.getConfig().getInt("electric_box_energy_given");

    /**
     * List of nearby objects. Is updated every tick
     */
    private ArrayList<Attackable> nearObjects = new ArrayList<>();

    public ElectricBox() {
        hp = maxHp;
    }

    public ElectricBox(Document document) {
        super(document);
        hp = document.getInteger("hp");
    }

    /**
     * Currently has no effect
     */
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

    /**
     * Currently has no effect
     */
    @Override
    public void setMaxHp(int hp) {
        //No op
    }

    /**
     * Currently has no effect
     */
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
        }
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    /**
     * Updates the current list nearby objects
     * <br>An object is considered 'nearby' if its Manhattan distance is {@literal <= @} 1 and is Attackable
     */
    private void updateNearObjects() {

        nearObjects.clear();

        for (GameObject object : getWorld().getGameObjects()) {
            if (object != this && object instanceof Attackable && Util.manhattanDist(object.getX(), object.getY(),
                    getX(), getY()) <= 1) {
                nearObjects.add((Attackable) object);
            }
        }
    }

    /**
     * Called every tick
     */
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
    public JSONObject jsonSerialise() {
        JSONObject json = super.jsonSerialise();

        json.put("hp", hp);

        return json;
    }

    @Override
    public Document mongoSerialise() {
        Document dbObject = super.mongoSerialise();

        dbObject.put("hp", getHp());

        return dbObject;
    }

    @Override
    public boolean onDeadCallback() {
        getWorld().decUpdatable();
        return false;
    }
}
