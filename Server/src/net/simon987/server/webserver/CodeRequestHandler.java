package net.simon987.server.webserver;

import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

public class CodeRequestHandler implements MessageHandler {
    @Override
    public void handle(OnlineUser user, JSONObject json) {

        if(json.get("t").equals("codeRequest")){

            LogManager.LOGGER.info("(WS) Code request from " + user.getUser().getUsername());

            JSONObject response = new JSONObject();

            response.put("t", "code");
            response.put("code", user.getUser().getUserCode());

            user.getWebSocket().send(response.toJSONString());

        }
    }
}
