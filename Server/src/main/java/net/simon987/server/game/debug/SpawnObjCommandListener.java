package net.simon987.server.game.debug;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import net.simon987.server.GameServer;
import net.simon987.server.event.DebugCommandEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.World;

import java.util.Arrays;

public class SpawnObjCommandListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;
        if (e.getName().equals("spawnObj")) {

            try {
                World world = GameServer.INSTANCE.getGameUniverse().getWorld(e.getInt("worldX"), e.getInt("worldY"),
                        false, e.getString("dimension"));

                DBObject dbObj = (DBObject) JSON.parse(e.getString("data"));
                dbObj.put("i", GameServer.INSTANCE.getGameUniverse().getNextObjectId());

                GameObject object = GameObject.deserialize(dbObj);

                if (object != null) {
                    world.addObject(object);
                    object.setWorld(world);

                    object.initialize();

                    e.reply("Created object " + object.getObjectId());

                } else {
                    e.reply("Couldn't deserialise the object");
                }

            } catch (Exception ex) {
                String message = ex.getMessage();
                message += "\n " + Arrays.toString(ex.getStackTrace()).replaceAll(", ", "\n");
                e.reply(message);
            }
        }

    }
}
