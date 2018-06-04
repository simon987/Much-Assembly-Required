package net.simon987.server.game.debug;

import net.simon987.server.GameServer;
import net.simon987.server.event.DebugCommandEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.Attackable;
import net.simon987.server.game.objects.GameObject;


public class DamageObjCommandListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("damageObj")) {

            GameObject object = GameServer.INSTANCE.getGameUniverse().getObject(e.getObjectId("objectId"));

            if (object != null) {

                if (object instanceof Attackable) {

                    int oldHp = ((Attackable) object).getHp();
                    int maxHp = ((Attackable) object).getMaxHp();
                    ((Attackable) object).damage(e.getInt("amount"));

                    e.reply("Success: " + oldHp + "/" + maxHp + " -> " + ((Attackable) object).getHp() + "/" + maxHp);
                } else {
                    e.reply("Object is not Attackable");
                }

            } else {
                e.reply("Object not found: " + e.getLong("objectId"));
            }
        }
    }

}
