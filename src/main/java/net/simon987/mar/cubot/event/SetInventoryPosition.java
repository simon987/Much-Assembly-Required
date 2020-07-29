package net.simon987.mar.cubot.event;

import net.simon987.mar.cubot.Cubot;
import net.simon987.mar.cubot.CubotInventory;
import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.event.DebugCommandEvent;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import net.simon987.mar.server.game.objects.GameObject;

public class SetInventoryPosition implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("setInventoryPosition")) {

            GameObject object = GameServer.INSTANCE.getUniverse().getObject(e.getObjectId("objectId"));

            if (object != null) {

                if (object instanceof Cubot) {

                    int position = e.getInt("position");
                    CubotInventory inventory = (CubotInventory) ((Cubot) object).getHardware(CubotInventory.class);

                    inventory.setPosition(position);
                    e.reply("Set inventory position to " + position);
                } else {
                    e.reply("Object is not a Cubot");
                }

            } else {
                e.reply("Object not found: " + e.getLong("objectId"));
            }
        }
    }
}
