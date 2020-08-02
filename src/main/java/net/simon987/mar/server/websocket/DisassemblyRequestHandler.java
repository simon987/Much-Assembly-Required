package net.simon987.mar.server.websocket;

import net.simon987.mar.server.logging.LogManager;
import org.json.simple.JSONObject;

import java.io.IOException;

public class DisassemblyRequestHandler implements MessageHandler {
    @Override
    public void handle(OnlineUser user, JSONObject json) throws IOException {

        if (json.get("t").equals("disassemblyRequest")) {

            LogManager.LOGGER.fine("(WS) Disassembly request from " + user.getUser().getUsername());

            if (user.getUser().isGuest()) {
                return;
            }

            JSONObject response = new JSONObject();

            response.put("t", "disassembly");
            response.put("lines", user.getUser().getDisassembly());

            user.getWebSocket().getRemote().sendString(response.toJSONString());
        }
    }
}
