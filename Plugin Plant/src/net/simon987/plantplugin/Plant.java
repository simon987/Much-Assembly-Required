package net.simon987.plantplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.InventoryHolder;
import net.simon987.server.game.Updatable;
import org.json.simple.JSONObject;

public class Plant extends GameObject implements Updatable, InventoryHolder{

    private static final char MAP_INFO = 0x4000;
    public static final int ID = 2;

    /**
     * Grow time (see config.properties)
     */
    private static final int GROW_TIME = GameServer.INSTANCE.getConfig().getInt("plant_grow_time");

    /**
     * Game time of the creation of this Plant
     */
    private long creationTime;

    /**
     * Whether the plant is grown or not
     */
    private boolean grown;

    /**
     * Yield of the plant, in biomass units
     */
    private int biomassCount;
    /**
     * Style of the plant (Only visual)
     */
    private int style;

    private static final int ITM_BIOMASS = 1;

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();

        json.put("type", ID);
        json.put("id", getObjectId());
        json.put("x", getX());
        json.put("y", getY());
        json.put("creationTime", creationTime);
        json.put("grown", grown);
        json.put("biomassCount", biomassCount);
        json.put("style", style);

        return json;
    }

    /**
     * Called every tick
     */
    @Override
    public void update() {
        if (!grown) {
            //Check grow
            if (creationTime + GROW_TIME <= GameServer.INSTANCE.getGameUniverse().getTime()) {
                grown = true;
                biomassCount = GameServer.INSTANCE.getConfig().getInt("plant_yield");
            }
        }
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isGrown() {
        return grown;
    }

    public void setGrown(boolean grown) {
        this.grown = grown;
    }

    public int getBiomassCount() {
        return biomassCount;
    }

    public void setBiomassCount(int biomassCount) {
        this.biomassCount = biomassCount;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public static Plant deserialize(JSONObject json){

        Plant plant = new Plant();

        plant.setObjectId((int)(long)json.get("id"));
        plant.setX((int)(long)json.get("x"));
        plant.setY((int)(long)json.get("y"));
        plant.grown = (boolean)json.get("grown");
        plant.creationTime = (long)json.get("creationTime");
        plant.style = (int)(long)json.get("style");
        plant.biomassCount = (int)(long)json.get("biomassCount");

        return plant;
    }

    /**
     * Called when an object attempts to place an item in this Plant
     *
     * @param item item id (see MarConstants.ITEM_*)
     * @return Always returns false
     */
    @Override
    public boolean placeItem(int item) {
        //Why would you want to place an item in a plant?
        return false;
    }

    /**
     * Called when an object attempts to take an item from this Plant.
     * If the object requests biomass, it will be subtracted from biomassCount, and
     * if it reaches 0, the plant is deleted
     *
     * @param item item id (see MarConstants.ITEM_*)
     * @return true if the requested item is ITEM_BIOMASS and if the plant is grown
     */
    @Override
    public boolean takeItem(int item) {

        if (item == ITM_BIOMASS) {
            if (grown && biomassCount > 1) {
                biomassCount--;
                return true;
            } else if (grown) {
                //Delete plant
                setDead(true);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }
}
