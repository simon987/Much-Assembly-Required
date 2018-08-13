package net.simon987.biomassplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.Rechargeable;
import org.bson.Document;

public class ItemBiomass extends Item {

    public static final int ID = 0x0001;

    private static final int energy = GameServer.INSTANCE.getConfig().getInt("biomassEnergyValue");

    @Override
    public int getId() {
        return ID;
    }

    public ItemBiomass() {
        super(null);
    }

    public ItemBiomass(Document document) {
        super(document);
    }

    @Override
    public void clear(ControllableUnit unit) {
        if (unit instanceof Rechargeable) {
            ((Rechargeable) unit).storeEnergy(energy);
        }
    }

    @Override
    public char poll() {
        return ID;
    }
}
