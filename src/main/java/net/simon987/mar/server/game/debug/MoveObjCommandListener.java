package net.simon987.mar.server.game.debug;

import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.event.DebugCommandEvent;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import net.simon987.mar.server.game.objects.GameObject;

public class MoveObjCommandListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("moveObj")) {

            GameObject object = GameServer.INSTANCE.getUniverse().getObject(e.getObjectId("objectId"));

            if (object != null) {

                object.setX(e.getInt("x"));
                object.setY(e.getInt("y"));

                e.reply("Success");
            } else {
                e.reply("Object not found: " + e.getLong("objectId"));
            }
        }
    }

}
