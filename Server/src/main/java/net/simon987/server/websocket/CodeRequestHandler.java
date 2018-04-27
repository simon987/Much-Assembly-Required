package net.simon987.server.websocket;

import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

import java.io.IOException;

public class CodeRequestHandler implements MessageHandler {
    @Override
    public void handle(OnlineUser user, JSONObject json) throws IOException {

        if (json.get("t").equals("codeRequest")) {

            LogManager.LOGGER.fine("(WS) Code request from " + user.getUser().getUsername());

            if (user.isGuest()) {

                JSONObject response = new JSONObject();

                response.put("t", "code");
                response.put("code", "; Create a free account to control your own Cubot with assembly language!"); //todo load from config

                user.getWebSocket().getRemote().sendString(response.toJSONString());

            } else {

                JSONObject response = new JSONObject();

                response.put("t", "code");
                response.put("code", user.getUser().getUserCode());

                user.getWebSocket().getRemote().sendString(response.toJSONString());
            }


        }
    }
}
