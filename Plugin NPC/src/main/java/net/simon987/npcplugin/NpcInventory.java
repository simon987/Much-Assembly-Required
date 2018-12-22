package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.ControllableUnit;
import org.bson.Document;


public class NpcInventory extends HardwareModule {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0106;

    public static final int DEFAULT_ADDRESS = 0x0106;

    private static final int INV_CLEAR = 0;
    private static final int INV_POLL = 1;
    private static final int INV_SCAN = 3;

    private Item item;

    public NpcInventory(ControllableUnit unit) {
        super(null, unit);
    }

    public NpcInventory(Document document, ControllableUnit cubot) {
        super(document, cubot);

        Document itemDoc = (Document) document.get("item");
        if (itemDoc != null) {
            item = GameServer.INSTANCE.getRegistry().deserializeItem(itemDoc);
        }
    }

    public void putItem(Item item) {
        this.item = item;
    }

    private void scanItem() {
        int x = getCpu().getRegisterSet().getRegister("X").getValue();
        item.digitize(unit.getCpu().getMemory(), x);
    }

    public Item clearItem() {

        Item oldItem = item;
        item.clear(unit);
        item = null;

        return oldItem;
    }

    @Override
    public char getId() {
        return HWID;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == INV_POLL) {
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

        if (item != null) {
            document.put("item", item.mongoSerialise());
        } else {
            document.put("item", null);
        }

        return document;
    }

    @Override
    public String toString() {
        return String.format("{NpcInventory [%s]}", item);
    }
}