package net.simon987.server.websocket;

import net.simon987.server.GameServer;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

import java.io.IOException;

public class CodeRequestHandler implements MessageHandler {
    @Override
    public void handle(OnlineUser user, JSONObject json) throws IOException {

        if (json.get("t").equals("codeRequest")) {

            LogManager.LOGGER.fine("(WS) Code request from " + user.getUser().getUsername());

            if (user.getUser().isGuest()) {

                JSONObject response = new JSONObject();

                response.put("t", "code");
                response.put("code", GameServer.INSTANCE.getConfig().getString("guest_user_code"));

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
