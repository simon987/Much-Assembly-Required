package net.simon987.biomassplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.InventoryHolder;
import org.json.simple.JSONObject;

public class BiomassBlob extends GameObject implements InventoryHolder {

    private static final char MAP_INFO = 0x4000;
    public static final int ID = 2;

    /**
     * Yield of the blob, in biomass units
     */
    private int biomassCount;
    /**
     * Style of the blob (Only visual)
     */
    //  private int style;

    private static final int ITM_BIOMASS = 1;

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();

        json.put("t", ID);
        json.put("i", getObjectId());
        json.put("x", getX());
        json.put("y", getY());
        json.put("b", biomassCount);
        //  json.put("style", style);

        return json;
    }

    @Override
    public BasicDBObject mongoSerialise() {

        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("t", ID);
        dbObject.put("i", getObjectId());
        dbObject.put("x", getX());
        dbObject.put("y", getY());
        dbObject.put("b", biomassCount);

        return dbObject;

    }

    public int getBiomassCount() {
        return biomassCount;
    }

    public void setBiomassCount(int biomassCount) {
        this.biomassCount = biomassCount;
    }

//    public int getStyle() {
//        return style;
//    }
//
//    public void setStyle(int style) {
//        this.style = style;
//    }

    public static BiomassBlob deserialize(DBObject obj) {

        BiomassBlob biomassBlob = new BiomassBlob();

        biomassBlob.setObjectId((long) obj.get("i"));
        biomassBlob.setX((int) obj.get("x"));
        biomassBlob.setY((int) obj.get("y"));
        //   biomassBlob.style = (int) json.get("style");
        biomassBlob.biomassCount = (int) obj.get("b");

        return biomassBlob;
    }

    /**
     * Called when an object attempts to place an item in this BiomassBlob
     *
     * @param item item id (see MarConstants.ITEM_*)
     * @return Always returns false
     */
    @Override
    public boolean placeItem(int item) {
        //Why would you want to place an item in a blob?
        return false;
    }

    @Override
    public boolean canTakeItem(int item) {
        return item == ITM_BIOMASS && biomassCount >= 1;
    }

    /**
     * Called when an object attempts to take an item from this BiomassBlob.
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
