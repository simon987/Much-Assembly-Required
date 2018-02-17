package net.simon987.server.webserver;

import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

public class DebugHandler implements MessageHandler {
    @Override
    public void handle(OnlineUser user, JSONObject json) {

        if (json.get("t").equals("debug") && user.isModerator()) {

            LogManager.LOGGER.fine("(WS) Debug command from " + user.getUser().getUsername());

            String command = (String) json.get("command");

            if (json.containsKey("command")) {

                JSONObject response = new JSONObject();

                switch (command) {

                    case "":
                        break;

                    default:
                        LogManager.LOGGER.severe("Unknown command: " + command);
                        response.put("t", "debug");
                        response.put("message", "Unknown command " + command);
                }

                user.getWebSocket().send(response.toJSONString());

            }

        }
    }
}
