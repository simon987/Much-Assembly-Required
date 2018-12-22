package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.ControllableUnit;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class CubotInventory extends HardwareModule {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0006;

    public static final int DEFAULT_ADDRESS = 6;

    private static final int INV_CLEAR = 0;
    private static final int INV_POLL = 1;
    private static final int INV_SEEK = 2;
    private static final int INV_SCAN = 3;

    private int inventorySize = 4; //TODO: load from config
    private Map<Integer, Item> inventory;
    private int position = 0;


    public CubotInventory(ControllableUnit unit) {
        super(null, unit);

        inventory = new HashMap<>();
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

    private void scanItem() {
        int x = getCpu().getRegisterSet().getRegister("X").getValue();
        Item item = inventory.get(position);
        item.digitize(unit.getCpu().getMemory(), x);
    }

    public Item clearItem() {
        Item item = inventory.get(position);
        item.clear(unit);
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
            if (unit.spendEnergy(100)) {
                clearItem();
            }
        } else if (a == INV_SEEK) {
            setPosition(getCpu().getRegisterSet().getRegister("X").getValue());
        } else if (a == INV_SCAN) {
            if (unit.spendEnergy(200)) {
                scanItem();
                clearItem();
            }
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
