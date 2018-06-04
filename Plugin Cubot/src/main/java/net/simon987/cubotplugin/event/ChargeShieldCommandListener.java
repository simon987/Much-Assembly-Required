package net.simon987.cubotplugin.event;

import net.simon987.cubotplugin.Cubot;
import net.simon987.server.GameServer;
import net.simon987.server.event.DebugCommandEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.GameObject;

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

            GameObject cubot = GameServer.INSTANCE.getGameUniverse().getObject(e.getObjectId("objectId"));

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
