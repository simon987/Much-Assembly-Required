package net.simon987.server.websocket;

import net.simon987.server.GameServer;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

import java.io.IOException;

public class UserInfoRequestHandler implements MessageHandler {


    @Override
    public void handle(OnlineUser user, JSONObject message) throws IOException {

        if (message.get("t").equals("userInfo")) {

            LogManager.LOGGER.fine("(WS) User info request from " + user.getUser().getUsername());
            JSONObject json = new JSONObject();

            if (user.getUser().isGuest()) {
                json.put("worldX", GameServer.INSTANCE.getConfig().getInt("new_user_worldX"));
                json.put("worldY", GameServer.INSTANCE.getConfig().getInt("new_user_worldY"));
                json.put("dimension", GameServer.INSTANCE.getConfig().getString("new_user_dimension"));

            } else {
                GameObject object = (GameObject) user.getUser().getControlledUnit();
                json.put("worldX", object.getWorld().getX());
                json.put("worldY", object.getWorld().getY());
                json.put("dimension", object.getWorld().getDimension());

            }

            json.put("t", "userInfo");
            json.put("maxWidth", GameServer.INSTANCE.getGameUniverse().getMaxWidth());
            user.getWebSocket().getRemote().sendString(json.toJSONString());

        }
    }
}
