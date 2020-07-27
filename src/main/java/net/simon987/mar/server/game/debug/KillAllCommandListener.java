package net.simon987.mar.server.game.debug;

import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.event.DebugCommandEvent;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import net.simon987.mar.server.game.objects.GameObject;
import net.simon987.mar.server.game.world.World;

import java.util.ArrayList;
import java.util.Arrays;

public class KillAllCommandListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("killAll")) {

            World world = GameServer.INSTANCE.getGameUniverse().getWorld(e.getInt("worldX"), e.getInt("worldY"),
                    false, e.getString("dimension"));

            try {

                ArrayList<GameObject> objs = world.getGameObjectsAt(e.getInt("x"), e.getInt("y"));

                for (GameObject o : objs) {
                    o.setDead(true);
                }

            } catch (Exception ex) {
                String message = ex.getMessage();
                message += "\n " + Arrays.toString(ex.getStackTrace()).replaceAll(", ", "\n");
                e.reply(message);
            }
        }
    }

}
