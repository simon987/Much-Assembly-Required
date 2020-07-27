package net.simon987.mar.server.game.debug;

import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.event.DebugCommandEvent;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import net.simon987.mar.server.game.world.World;

public class SetTileAtCommandListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("setTileAt")) {

            World world = GameServer.INSTANCE.getGameUniverse().getWorld(e.getInt("worldX"), e.getInt("worldY"),
                    false, e.getString("dimension"));

            if (world != null) {

                world.getTileMap().setTileAt(e.getInt("newTile"), e.getInt("x"), e.getInt("y"));
                e.reply("Success");

            } else {
                e.reply("Error: World is uncharted");
            }
        }
    }
}
