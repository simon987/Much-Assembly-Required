package net.simon987.server.webserver;

import net.simon987.server.game.GameObject;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

public class UserInfoRequestHandler implements MessageHandler {


    @Override
    public void handle(OnlineUser user, JSONObject message) {

        if (message.get("t").equals("userInfo")) {
            LogManager.LOGGER.info("(WS) User info request from " + user.getUser().getUsername());

            GameObject object = (GameObject)user.getUser().getControlledUnit();

            JSONObject json = new JSONObject();
            json.put("t", "userInfo");
            json.put("worldX", object.getWorld().getX());
            json.put("worldY", object.getWorld().getY());
            json.put("x", object.getX());
            json.put("y", object.getY());

            user.getWebSocket().send(json.toJSONString());

        }
    }
}
