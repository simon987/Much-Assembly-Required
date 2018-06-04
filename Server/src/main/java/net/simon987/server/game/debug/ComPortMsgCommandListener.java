package net.simon987.server.game.debug;

import net.simon987.server.GameServer;
import net.simon987.server.event.DebugCommandEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.MessageReceiver;
import org.bson.types.ObjectId;

public class ComPortMsgCommandListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("comPortMsg")) {

            ObjectId objectId = e.getObjectId("objectId");

            GameObject object = GameServer.INSTANCE.getGameUniverse().getObject(objectId);

            if (object != null) {

                if (object instanceof MessageReceiver) {

                    e.reply("Result: " + ((MessageReceiver) object).sendMessage(e.getString("message").toCharArray()));

                } else {
                    e.reply("Object " + objectId + " not MessageReceiver");
                }

            } else {
                e.reply("Object " + objectId + " not found");
            }
        }
    }

}
