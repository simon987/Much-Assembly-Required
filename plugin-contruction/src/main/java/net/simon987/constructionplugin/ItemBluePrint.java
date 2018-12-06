package net.simon987.constructionplugin;

import net.simon987.server.assembly.Memory;
import net.simon987.server.game.item.Item;
import org.bson.Document;
import org.json.simple.JSONObject;

public class ItemBluePrint extends Item {

    public static final int ID = 0x0005;
    private Class<? extends BluePrint> bluePrint;

    public ItemBluePrint() {
        super(null);
    }

    public ItemBluePrint(Document document) {
        super(document);
        try {
            bluePrint = Class.forName(document.getString("blueprint")).asSubclass(BluePrint.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void digitize(Memory memory, int offset) {
        char[] data = BluePrintUtil.bluePrintData(bluePrint);
        memory.write(offset, data, 0, data.length);

        System.out.println("DEBUG: blueprint digitize " + data.length);
    }

    @Override
    public char poll() {
        return ID;
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public JSONObject debugJsonSerialise() {
        JSONObject json = super.debugJsonSerialise();
        json.put("blueprint", bluePrint.getCanonicalName());
        return json;
    }

    @Override
    public Document mongoSerialise() {
        Document document = super.mongoSerialise();

        document.put("blueprint", bluePrint.getCanonicalName());
        return document;
    }
}
