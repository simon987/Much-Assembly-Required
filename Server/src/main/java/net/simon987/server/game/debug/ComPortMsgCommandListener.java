package net.simon987.server.game.debug;

import net.simon987.server.GameServer;
import net.simon987.server.event.DebugCommandEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.Programmable;

public class ComPortMsgCommandListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("comPortMsg")) {

            long objectId = e.getLong("objectId");

            GameObject object = GameServer.INSTANCE.getGameUniverse().getObject(objectId);

            if (object != null) {

                if (object instanceof Programmable) {

                    e.reply("Result: " + ((Programmable) object).sendMessage(e.getString("message").toCharArray()));

                } else {
                    e.reply("Object " + objectId + " not Programmable");
                }

            } else {
                e.reply("Object " + objectId + " not found");
            }
        }
    }

}
