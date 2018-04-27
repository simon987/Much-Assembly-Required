package net.simon987.server.websocket;

import org.json.simple.JSONObject;

import java.io.IOException;

public interface MessageHandler {

    void handle(OnlineUser user, JSONObject json) throws IOException;

}
