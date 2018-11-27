package net.simon987.biomassplugin;

import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.InventoryHolder;
import org.bson.Document;
import org.json.simple.JSONObject;

public class BiomassBlob extends GameObject implements InventoryHolder {

    private static final char MAP_INFO = 0x0101;

    /**
     * Yield of the blob, in biomass units
     */
    private int biomassCount;

    public BiomassBlob() {
    }

    public BiomassBlob(Document document) {
        super(document);

        biomassCount = document.getInteger("biomassCount");
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

        dbObject.put("biomassCount", biomassCount);

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
     * @return Always returns false
     */
    @Override
    public boolean placeItem(Item item) {
        //Why would you want to place an item in a blob?
        return false;
    }

    @Override
    public boolean canTakeItem(int itemId) {
        return itemId == ItemBiomass.ID && biomassCount >= 1;
    }

    /**
     * Called when an object attempts to take an item from this BiomassBlob.
     * If the object requests biomass, it will be subtracted from biomassCount, and
     * if it reaches 0, the plant is deleted
     */
    @Override
    public void takeItem(int itemId) {

        if (itemId == ItemBiomass.ID) {
            if (biomassCount > 1) {
                biomassCount--;
            } else {
                //Delete plant
                setDead(true);
            }
        }
    }
}
