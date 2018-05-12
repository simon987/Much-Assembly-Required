package net.simon987.server.websocket;

import org.json.simple.JSONObject;

import java.util.ArrayList;


public class KeypressHandler implements MessageHandler {

    @Override
    public void handle(OnlineUser user, JSONObject json) {

        if (!user.getUser().isGuest()) {
            if (json.get("t").equals("k")) {

                //LogManager.LOGGER.fine("(WS) Received keypress");

                int key = (int) (long) json.get("k");

                ArrayList<Integer> buffer = user.getUser().getControlledUnit().getKeyboardBuffer();

                if (buffer.size() < 16) {
                    buffer.add(key);
                }
            }
        }
    }
}
