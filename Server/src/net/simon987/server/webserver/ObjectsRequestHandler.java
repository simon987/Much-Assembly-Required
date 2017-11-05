package net.simon987.server.webserver;

import net.simon987.server.GameServer;
import net.simon987.server.game.GameObject;
import net.simon987.server.io.JSONSerialisable;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class ObjectsRequestHandler implements MessageHandler {



    @Override
    public void handle(OnlineUser user, JSONObject json) {
        if (json.get("t").equals("object")) {
            LogManager.LOGGER.info("(WS) Objects request from " + user.getUser().getUsername());


            if (json.containsKey("x") && json.containsKey("y")) {
                int x = Long.valueOf((long) json.get("x")).intValue();
                int y = Long.valueOf((long) json.get("y")).intValue();

                ArrayList<GameObject> gameObjects = GameServer.INSTANCE.getGameUniverse().getWorld(x, y).getGameObjects();

                JSONObject response = new JSONObject();
                JSONArray objects = new JSONArray();


                for (GameObject object : gameObjects) {

                    if (object instanceof JSONSerialisable) {
                        objects.add(object.serialise());
                    }

                }

                response.put("t", "object");
                response.put("objects", objects);


                if (user.getWebSocket().isOpen()) {
                    user.getWebSocket().send(response.toJSONString());
                }
            } else {
                LogManager.LOGGER.info("(WS) Malformed Objects request from " + user.getUser().getUsername());
            }
        }
    }
}
