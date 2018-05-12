package net.simon987.biomassplugin;

import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.InventoryHolder;
import org.bson.Document;
import org.json.simple.JSONObject;

public class BiomassBlob extends GameObject implements InventoryHolder {

    private static final char MAP_INFO = 0x4000;

    /**
     * Yield of the blob, in biomass units
     */
    private int biomassCount;
    /**
     * Style of the blob (Only visual)
     */
    //  private int style;

    private static final int ITM_BIOMASS = 1;

    public BiomassBlob() {
    }

    public BiomassBlob(Document document) {
        super(document);

        biomassCount = document.getInteger("b");
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public JSONObject jsonSerialise() {

        JSONObject json = super.jsonSerialise();

        json.put("b", biomassCount);

        return json;
    }

    @Override
    public Document mongoSerialise() {

        Document dbObject = super.mongoSerialise();

        dbObject.put("b", biomassCount);

        return dbObject;

    }

    public int getBiomassCount() {
        return biomassCount;
    }

    public void setBiomassCount(int biomassCount) {
        this.biomassCount = biomassCount;
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
