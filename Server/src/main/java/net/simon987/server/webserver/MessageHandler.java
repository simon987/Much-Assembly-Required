package net.simon987.server.webserver;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.simple.JSONObject;

public interface MessageHandler {

    void handle(OnlineUser user, JSONObject json) throws WebsocketNotConnectedException;

}
