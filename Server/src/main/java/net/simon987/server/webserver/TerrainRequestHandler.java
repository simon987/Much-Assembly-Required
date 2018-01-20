package net.simon987.server.webserver;

import net.simon987.server.GameServer;
import net.simon987.server.game.World;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TerrainRequestHandler implements MessageHandler {

    @Override
    public void handle(OnlineUser user, JSONObject json) {
        if (json.get("t").equals("terrain") && json.containsKey("x") && json.containsKey("y")) {

//            LogManager.LOGGER.fine("Terrain request from " + user.getUser().getUsername());
            World world;
            try {
                world = GameServer.INSTANCE.getGameUniverse().getWorld(
                        Long.valueOf((long) json.get("x")).intValue(),
                        Long.valueOf((long) json.get("y")).intValue(), false);
            } catch (NullPointerException e) {
                LogManager.LOGGER.severe("FIXME: handle TerrainRequestHandler");
                return;
            }


            //todo It might be a good idea to cache this
            if (world != null) {
                JSONObject response = new JSONObject();

                JSONArray terrain = new JSONArray();

                int[][] tiles = world.getTileMap().getTiles();
                for (int x = 0; x < World.WORLD_SIZE; x++) {
                    for (int y = 0; y < World.WORLD_SIZE; y++) {
                        terrain.add(tiles[y][x]);
                    }
                }

                response.put("t", "terrain");
                response.put("ok", true);
                response.put("terrain", terrain);

                user.getWebSocket().send(response.toJSONString());
            } else {
                //Uncharted World
                JSONObject response = new JSONObject();
                response.put("t", "terrain");
                response.put("ok", false);

                user.getWebSocket().send(response.toJSONString());
            }
        }
    }
}
