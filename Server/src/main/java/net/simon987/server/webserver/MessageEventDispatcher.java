package net.simon987.server.webserver;


import net.simon987.server.logging.LogManager;
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

            if (json.containsKey("t")) {
                for (MessageHandler handler : handlers) {
                    handler.handle(user, json);
                }
            } else {
                LogManager.LOGGER.info("Malformed JSON sent by " + user.getUser().getUsername());
            }

        } catch (ParseException e) {
            LogManager.LOGGER.info("Malformed JSON sent by " + user.getUser().getUsername());
        }
    }
}
