package net.simon987.mar.server.websocket;

import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.event.DebugCommandEvent;
import org.json.simple.JSONObject;

public class DebugCommandHandler implements MessageHandler {

    @Override
    public void handle(OnlineUser user, JSONObject json) {


        if (json.get("t").equals("debug") && user.getUser().isModerator()) {

            DebugCommandEvent e = new DebugCommandEvent(json, user);
            GameServer.INSTANCE.getEventDispatcher().dispatch(e); //Ignore cancellation
        }
    }
}
