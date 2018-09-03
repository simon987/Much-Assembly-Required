package net.simon987.server.web;

import net.simon987.server.GameServer;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

public class ServerInfoRoute implements Route {

    private String address;
    private String serverName;
    private int tickLength;

    public ServerInfoRoute() {

        //Info variables that don't change
        address = GameServer.INSTANCE.getConfig().getString("mar_address");
        serverName = GameServer.INSTANCE.getConfig().getString("server_name");
        tickLength = GameServer.INSTANCE.getConfig().getInt("tick_length");
    }

    @Override
    public Object handle(Request request, Response response) {

        JSONObject json = new JSONObject();

        String username = request.session().attribute("username");

        if (username != null) {
            String token = GameServer.INSTANCE.getUserManager().generateAndGetToken(username);

            json.put("token", token);
            json.put("username", username);

        } else {

            json.put("token",
                    "00000000000000000000000000000000" +
                            "00000000000000000000000000000000" +
                            "00000000000000000000000000000000" +
                            "00000000000000000000000000000000");
            json.put("username", "guest");
        }

        json.put("address", address);
        json.put("serverName", serverName);
        json.put("tickLength", tickLength);

        response.header("Content-Type", "application/json");

        return json.toJSONString();
    }
}
