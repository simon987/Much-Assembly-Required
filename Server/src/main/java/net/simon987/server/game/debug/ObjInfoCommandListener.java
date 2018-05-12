package net.simon987.server.game.debug;

import net.simon987.server.GameServer;
import net.simon987.server.event.DebugCommandEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.world.World;

import java.util.Arrays;
import java.util.Collection;

public class ObjInfoCommandListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("objInfo")) {

            World world = GameServer.INSTANCE.getGameUniverse().getWorld(e.getInt("worldX"), e.getInt("worldY"),
                    false, e.getString("dimension"));

            try {

                Collection<GameObject> objs = world.getGameObjects();
                String str = objs.size() + "\n";

                for (GameObject obj : objs) {

                    if (obj.isAt(e.getInt("x"), e.getInt("y")) || (obj.getX() == e.getInt("x") &&
                            obj.getY() == e.getInt("y"))) {
                        str += "Mongo:" + obj.mongoSerialise() + "\n";
                        str += "JSON :" + obj.jsonSerialise().toJSONString() + "\n\n";
                    }
                }

                e.reply(str);

            } catch (Exception ex) {
                String message = ex.getMessage();
                message += "\n " + Arrays.toString(ex.getStackTrace()).replaceAll(", ", "\n");
                e.reply(message);
            }
        }
    }

}
