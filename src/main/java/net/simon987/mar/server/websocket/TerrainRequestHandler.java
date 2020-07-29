package net.simon987.mar.server.websocket;

import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.game.world.World;
import net.simon987.mar.server.logging.LogManager;
import org.json.simple.JSONObject;

import java.io.IOException;

public class TerrainRequestHandler implements MessageHandler {

    private final boolean createNewOnRequest;

    public TerrainRequestHandler(boolean createNewOnRequest) {
        this.createNewOnRequest = createNewOnRequest;
    }

    @Override
    public void handle(OnlineUser user, JSONObject json) throws IOException {
        if (json.get("t").equals("terrain") && json.containsKey("x") && json.containsKey("y") &&
                json.containsKey("dimension")) {

            LogManager.LOGGER.fine("Terrain request from " + user.getUser().getUsername());
            World world;
            try {
                world = GameServer.INSTANCE.getUniverse().getWorld(
                        Long.valueOf((long) json.get("x")).intValue(),
                        Long.valueOf((long) json.get("y")).intValue(),
                        createNewOnRequest,
                        (String) json.get("dimension"));
            } catch (NullPointerException e) {
                LogManager.LOGGER.severe("FIXME: handle TerrainRequestHandler");
                return;
            }

            //todo It might be a good idea to cache this
            JSONObject response;
            if (world != null) {
                response = world.getTileMap().jsonSerialise();

                response.put("t", "terrain");
                response.put("ok", true);
                response.put("size", world.getWorldSize());

            } else {
                //Uncharted World
                response = new JSONObject();
                response.put("t", "terrain");
                response.put("ok", false);
            }

            user.getWebSocket().getRemote().sendString(response.toJSONString());
        }
    }
}
