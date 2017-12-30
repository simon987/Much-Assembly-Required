package net.simon987.server.webserver;

import net.simon987.server.GameServer;
import net.simon987.server.game.GameObject;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

public class UserInfoRequestHandler implements MessageHandler {


    @Override
    public void handle(OnlineUser user, JSONObject message) {

        if (message.get("t").equals("userInfo")) {

            LogManager.LOGGER.fine("(WS) User info request from " + user.getUser().getUsername());
            JSONObject json = new JSONObject();

            if (user.isGuest()) {
                json.put("worldX", GameServer.INSTANCE.getConfig().getInt("new_user_worldX"));
                json.put("worldY", GameServer.INSTANCE.getConfig().getInt("new_user_worldY"));

            } else {
                GameObject object = (GameObject) user.getUser().getControlledUnit();
                json.put("worldX", object.getWorld().getX());
                json.put("worldY", object.getWorld().getY());

            }

            json.put("t", "userInfo");
            json.put("maxWidth", GameServer.INSTANCE.getGameUniverse().getMaxWidth());
            user.getWebSocket().send(json.toJSONString());


        }
    }
}
