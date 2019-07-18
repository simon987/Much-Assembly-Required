package net.simon987.constructionplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.*;
import net.simon987.server.game.world.World;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;

public class ConstructionSite extends Structure implements Updatable, InventoryHolder {

    public static final int MAP_INFO = 0xFFFF; //TODO: determine
    public static final int LIFETIME = GameServer.INSTANCE.getConfig().getInt("construction_site_ttl");

    private int age;
    private BluePrint bluePrint;

    public ConstructionSite(BluePrint bluePrint) {
        super(1, 1);

        this.bluePrint = bluePrint;
        this.age = 0;
    }

    public ConstructionSite(Document document) {
        super(document, 1, 1);

        age = document.getInteger("age");
        bluePrint = BluePrintRegistry.INSTANCE.deserializeBlueprint((Document) document.get("blueprint"));
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public void update() {

        age += 1;

        if (age > LIFETIME) {
            setDead(true);
        }
    }

    @Override
    public boolean placeItem(Item item) {
        boolean ok = bluePrint.placeItem(item);

        if (ok && bluePrint.isCompleted()) {
            instantiateTargetObject();
            setDead(true);
        }

        return ok;
    }

    private void instantiateTargetObject() {
        Class<? extends GameObject> targetClass = bluePrint.getTargetObject();
        try {
            GameObject targetObj = targetClass.getConstructor().newInstance();

            targetObj.setWorld(getWorld());
            targetObj.setObjectId(new ObjectId());
            targetObj.setX(getX());
            targetObj.setY(getY());

            getWorld().addObject(targetObj);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canPlaceItem(int itemId) {
        return bluePrint.canPlaceItem(itemId);
    }

    @Override
    public void takeItem(int itemId) {
        //NOOP
    }

    @Override
    public boolean canTakeItem(int itemId) {
        return false;
    }

    @Override
    public JSONObject jsonSerialise() {
        JSONObject json = super.jsonSerialise();

        json.put("blueprint", bluePrint.jsonSerialise());
        json.put("age", age);

        return json;
    }

    @Override
    public Document mongoSerialise() {
        Document document = super.mongoSerialise();

        document.put("blueprint", bluePrint.mongoSerialise());
        document.put("age", age);

        return document;
    }
}
