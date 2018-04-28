package net.simon987.server.websocket;

import net.simon987.server.GameServer;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

public class FloppyHandler implements MessageHandler {

    SocketServerDatabase db = new SocketServerDatabase(GameServer.INSTANCE.getConfig());

    @Override
    public void handle(OnlineUser user, JSONObject json) {

        if (json.get("t").equals("floppyDown")) {

            LogManager.LOGGER.fine("(WS) Floppy download request from " + user.getUser().getUsername());

            if (user.getUser().isGuest()) {
                return;
            }

            if (user.getUser().getControlledUnit().getFloppyData() != null) {
                byte[] bytes = user.getUser().getControlledUnit().getFloppyData().getBytes();
                LogManager.LOGGER.severe("TODO FloppyHandler.handle()");
            }


        } else if (json.get("t").equals("floppyUp")) {

            LogManager.LOGGER.fine("(WS) Floppy upload request from " + user.getUser().getUsername());

            //Check newly uploaded file on the database
            byte[] bytes = db.getFloppy(user.getUser().getUsername());

            if (bytes != null) {
                user.getUser().getControlledUnit().getFloppyData().setBytes(bytes);
            }
        }
    }
}
