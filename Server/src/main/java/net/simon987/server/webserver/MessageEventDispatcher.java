package net.simon987.server.webserver;


import net.simon987.server.logging.LogManager;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class MessageEventDispatcher {

    private ArrayList<MessageHandler> handlers = new ArrayList<>(10);

    public MessageEventDispatcher() {

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
                    } catch (WebsocketNotConnectedException e) {
                        LogManager.LOGGER.fine("Catched WebsocketNotConnectedException");
                    } catch (Exception e1) {
                        LogManager.LOGGER.severe(e1.getMessage());
                        e1.printStackTrace();
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
