package net.simon987.server.webserver;

import net.simon987.server.GameServer;
import net.simon987.server.event.DebugCommandEvent;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.simple.JSONObject;

public class DebugCommandHandler implements MessageHandler {

    @Override
    public void handle(OnlineUser user, JSONObject json) throws WebsocketNotConnectedException {


        if (json.get("t").equals("debug") && user.isModerator()) {

            DebugCommandEvent e = new DebugCommandEvent(json, user);
            GameServer.INSTANCE.getEventDispatcher().dispatch(e); //Ignore cancellation
        }
    }
}
