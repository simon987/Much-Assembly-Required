package net.simon987.server.webserver;

import net.simon987.server.GameServer;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.World;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ObjectsRequestHandler implements MessageHandler {


    @Override
    public void handle(OnlineUser user, JSONObject json) {
        if (json.get("t").equals("object")) {
            // LogManager.LOGGER.fine("(WS) Objects request from " + user.getUser().getUsername());

            int x, y;
            try {
                x = Long.valueOf((long) json.get("x")).intValue();
                y = Long.valueOf((long) json.get("y")).intValue();
            } catch (Exception e) {
                LogManager.LOGGER.severe("(WS) Malformed Objects request from " + user.getUser().getUsername());
                return;
            }

            World world = GameServer.INSTANCE.getGameUniverse().getWorld(x, y, false);

            if (world != null) {

                JSONObject response = new JSONObject();
                JSONArray objects = new JSONArray();


                for (GameObject object : world.getGameObjects()) {
                    objects.add(object.serialise());
                }

                response.put("t", "object");
                response.put("objects", objects);


                if (user.getWebSocket().isOpen()) {
                    user.getWebSocket().send(response.toJSONString());
                }
            }
        }
    }
}
