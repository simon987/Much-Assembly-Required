package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.item.ItemCopper;
import net.simon987.server.game.objects.ControllableUnit;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class CubotInventory extends CubotHardwareModule {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0006;

    public static final int DEFAULT_ADDRESS = 6;

    private static final int INV_CLEAR = 0;
    private static final int INV_POLL = 1;

    private int inventorySize = 4;

    private Map<Integer, Item> inventory;
    private int position = 0;

    public CubotInventory(Cubot cubot) {
        super(cubot);

        inventory = new HashMap<>();
        inventory.put(2, new ItemCopper(new Document()));  // TODO: Remove debug value
    }

    public CubotInventory(Document document, ControllableUnit cubot) {
        super(document, cubot);

        position = document.getInteger("position");
        inventorySize = document.getInteger("size");

        inventory = new HashMap<>();
        for (String i : ((Map<String, Document>) document.get("inventory")).keySet()) {
            inventory.put(Integer.valueOf(i),
                    GameServer.INSTANCE.getRegistry().deserializeItem(((Map<String, Document>) document.get("inventory")).get(i)));
        }
    }

    public void putItem(Item item) {
        inventory.put(position, item);
    }

    public Item popItem() {
        Item item = inventory.get(position);
        item.clear(cubot);
        inventory.remove(position);

        return item;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int inventoryPosition) {
        this.position = inventoryPosition;
    }

    public Map<Integer, Item> getInventory() {
        return inventory;
    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == INV_POLL) {
            Item item = inventory.get(position);
            char result;
            if (item == null) {
                result = 0;
            } else {
                result = item.poll();
            }
            getCpu().getRegisterSet().getRegister("B").setValue(result);

        } else if (a == INV_CLEAR) {
            popItem();
        }

    }

    @Override
    public Document mongoSerialise() {
        Document document = super.mongoSerialise();

        document.put("position", position);
        document.put("size", inventorySize);

        Document items = new Document();

        for (Integer i : inventory.keySet()) {
            items.put(i.toString(), inventory.get(i).mongoSerialise());
        }

        document.put("inventory", items);

        return document;
    }

    @Override
    public String toString() {
        String itemList = "";
        for (Integer i : inventory.keySet()) {
            itemList += i + ": " + inventory.get(i).getClass().getSimpleName() + ", ";
        }
        return String.format("{CubotInventory[%d/%d] @ %d [%s]}", inventory.size(), inventorySize, position, itemList);
    }
}
