package net.simon987.server.webserver;

import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;


public class KeypressHandler implements MessageHandler {

    @Override
    public void handle(OnlineUser user, JSONObject json) {
        if(json.get("t").equals("k")){

            LogManager.LOGGER.info("(WS) Received keypress");

            int key = (int)(long)json.get("k");

            user.getUser().getControlledUnit().getKeyboardBuffer().add(key);
        }
    }
}
