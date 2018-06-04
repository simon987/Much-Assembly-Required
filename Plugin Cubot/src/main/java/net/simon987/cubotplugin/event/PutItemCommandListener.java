package net.simon987.cubotplugin.event;

import net.simon987.cubotplugin.Cubot;
import net.simon987.cubotplugin.CubotInventory;
import net.simon987.server.GameServer;
import net.simon987.server.event.DebugCommandEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.GameObject;
import org.bson.Document;

public class PutItemCommandListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("putItem")) {

            GameObject object = GameServer.INSTANCE.getGameUniverse().getObject(e.getObjectId("objectId"));

            if (object != null) {

                if (object instanceof Cubot) {

                    CubotInventory inventory = (CubotInventory) ((Cubot) object).getHardware(CubotInventory.class);
                    Item item = GameServer.INSTANCE.getRegistry().deserializeItem(Document.parse(e.getString("item")));

                    if (item != null) {
                        inventory.putItem(item);
                        e.reply("Set item to " + item.getClass().getSimpleName());

                    } else {
                        e.reply("Couldn't deserialize item");
                    }

                } else {
                    e.reply("Object is not a Cubot");
                }

            } else {
                e.reply("Object not found: " + e.getLong("objectId"));
            }
        }
    }
}
