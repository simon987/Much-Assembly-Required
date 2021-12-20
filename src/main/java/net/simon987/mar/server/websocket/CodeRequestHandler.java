package net.simon987.mar.server.websocket;

import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.logging.LogManager;
import org.json.simple.JSONObject;

import java.io.IOException;

public class CodeRequestHandler implements MessageHandler {
    @Override
    public void handle(OnlineUser user, JSONObject json) throws IOException {

        if (json.get("t").equals("codeRequest")) {

            LogManager.LOGGER.fine("(WS) Code request from " + user.getUser().getUsername());

            JSONObject response = new JSONObject();

            response.put("t", "code");

            String code = user.getUser().isGuest() ?
                    GameServer.INSTANCE.getConfig().getString("guest_user_code") :
                    user.getUser().getUserCode();

            response.put("code", code);
            user.getWebSocket().getRemote().sendString(response.toJSONString());
        }
    }
}
