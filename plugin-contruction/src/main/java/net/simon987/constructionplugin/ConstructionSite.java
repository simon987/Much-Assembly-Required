package net.simon987.constructionplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.InventoryHolder;
import net.simon987.server.game.objects.Updatable;

public class ConstructionSite extends GameObject implements Updatable, InventoryHolder {

    public static final int MAP_INFO = 0xFFFF; //TODO: determine
    public static final int LIFETIME = GameServer.INSTANCE.getConfig().getInt("construction_site_ttl");

    private int age;

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

        //todo: add mats here
        //todo: inv digitize

        return false;
    }

    @Override
    public void takeItem(int itemId) {
        //NOOP
    }

    @Override
    public boolean canTakeItem(int itemId) {
        return false;
    }
}
