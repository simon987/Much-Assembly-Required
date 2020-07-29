package net.simon987.mar.cubot.event;

import net.simon987.mar.cubot.Cubot;
import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.event.DebugCommandEvent;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import net.simon987.mar.server.game.objects.GameObject;

/**
 * Debug command to add shield points to a Cubot
 */
public class ChargeShieldCommandListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("chargeShield")) {

            GameObject cubot = GameServer.INSTANCE.getUniverse().getObject(e.getObjectId("objectId"));

            if (cubot != null) {

                if (cubot instanceof Cubot) {

                    String hp = ((Cubot) cubot).getHp() + "/" + ((Cubot) cubot).getMaxHp();
                    int oldShield = ((Cubot) cubot).getShield();
                    ((Cubot) cubot).chargeShield(e.getInt("amount"));

                    e.reply("Success: " + hp + " (" + oldShield + ") -> " + hp + "(" + ((Cubot) cubot).getShield() +
                            ")");
                } else {
                    e.reply("Object is not a Cubot");
                }

            } else {
                e.reply("Object not found: " + e.getLong("objectId"));
            }
        }
    }

}
