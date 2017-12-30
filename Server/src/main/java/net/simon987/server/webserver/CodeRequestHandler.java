package net.simon987.server.webserver;

import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

public class CodeRequestHandler implements MessageHandler {
    @Override
    public void handle(OnlineUser user, JSONObject json) {

        if (json.get("t").equals("codeRequest")) {

            LogManager.LOGGER.fine("(WS) Code request from " + user.getUser().getUsername());

            if (user.isGuest()) {

                JSONObject response = new JSONObject();

                response.put("t", "code");
                response.put("code", "; Create a free account to control your own Cubot with assembly language!"); //todo load from config

                user.getWebSocket().send(response.toJSONString());

            } else {

                JSONObject response = new JSONObject();

                response.put("t", "code");
                response.put("code", user.getUser().getUserCode());

                user.getWebSocket().send(response.toJSONString());
            }


        }
    }
}
