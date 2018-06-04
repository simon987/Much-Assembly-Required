package net.simon987.server.game.debug;

import net.simon987.server.GameServer;
import net.simon987.server.event.DebugCommandEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.Rechargeable;

public class SetEnergyCommandListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("setEnergy")) {

            GameObject object = GameServer.INSTANCE.getGameUniverse().getObject(e.getObjectId("objectId"));

            if (object != null) {

                if (object instanceof Rechargeable) {

                    int oldEnergy = ((Rechargeable) object).getEnergy();
                    ((Rechargeable) object).setEnergy(e.getInt("amount"));

                    e.reply("Success: " + oldEnergy + " -> " + e.getInt("amount"));
                } else {
                    e.reply("Object is not Rechargeable");
                }

            } else {
                e.reply("Object not found: " + e.getLong("objectId"));
            }
        }
    }

}
