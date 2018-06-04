package net.simon987.server.game.debug;

import net.simon987.server.GameServer;
import net.simon987.server.event.DebugCommandEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.Updatable;
import net.simon987.server.game.world.World;


public class TpObjectCommandListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("tpObj")) {

            GameObject object = GameServer.INSTANCE.getGameUniverse().getObject(e.getObjectId("objectId"));
            World world = GameServer.INSTANCE.getGameUniverse().getWorld(e.getInt("worldX"), e.getInt("worldY"),
                    false, e.getString("dimension"));

            if (object != null) {

                if (world != null) {

                    if (object instanceof Updatable) {
                        object.getWorld().decUpdatable();
                        world.incUpdatable();

                    }

                    object.getWorld().removeObject(object);
                    world.addObject(object);
                    object.setWorld(world);

                    object.setX(e.getInt("x"));
                    object.setY(e.getInt("y"));

                    e.reply("Success");

                } else {
                    e.reply("World not found");
                }
            } else {
                e.reply("Object not found");
            }
        }
    }

}
