package net.simon987.plantplugin;

import net.simon987.server.game.GameObject;
import net.simon987.server.game.InventoryHolder;
import org.json.simple.JSONObject;

public class Plant extends GameObject implements InventoryHolder {
    private static final char MAP_INFO = 0x4000;
    public static final int ID = 2;

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
        json.put("biomassCount", biomassCount);
        json.put("style", style);

        return json;
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

    public static Plant deserialize(JSONObject json) {

        Plant plant = new Plant();

        plant.setObjectId((int) (long) json.get("id"));
        plant.setX((int) (long) json.get("x"));
        plant.setY((int) (long) json.get("y"));
        plant.style = (int) (long) json.get("style");
        plant.biomassCount = (int) (long) json.get("biomassCount");

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

    @Override
    public boolean canTakeItem(int item) {
        return item == ITM_BIOMASS && biomassCount >= 1;
    }

    /**
     * Called when an object attempts to take an item from this Plant.
     * If the object requests biomass, it will be subtracted from biomassCount, and
     * if it reaches 0, the plant is deleted
     *
     * @param item item id (see MarConstants.ITEM_*)
     */
    @Override
    public void takeItem(int item) {

        if (item == ITM_BIOMASS) {
            if (biomassCount > 1) {
                biomassCount--;
            } else {
                //Delete plant
                setDead(true);
            }
        }

    }
}
