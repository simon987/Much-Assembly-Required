package net.simon987.server.websocket;


import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class MessageDispatcher {

    private ArrayList<MessageHandler> handlers = new ArrayList<>(10);

    public MessageDispatcher() {

    }

    public void addHandler(MessageHandler handler) {

        handlers.add(handler);
    }

    public void dispatch(OnlineUser user, String message) {

        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(message);

            if (json.containsKey("t") && user.getWebSocket().isOpen()) {
                for (MessageHandler handler : handlers) {
                    try {
                        handler.handle(user, json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        //Socket is closed
                    }
                }
            } else {
                LogManager.LOGGER.severe("Malformed JSON sent by " + user.getUser().getUsername());
            }

        } catch (ParseException e) {
            LogManager.LOGGER.severe("Malformed JSON sent by " + user.getUser().getUsername());
        }
    }
}
