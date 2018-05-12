package net.simon987.server.web;

import net.simon987.server.GameServer;
import net.simon987.server.logging.LogManager;
import spark.Request;
import spark.Response;
import spark.Route;

public class FloppyDownloadRoute implements Route {

    @Override
    public Object handle(Request request, Response response) {

        String username = request.session().attribute("username");

        if (username != null) {

            response.header("Content-Type", "application/octet-stream");
            response.header("Content-Disposition", "filename=\"floppy.bin\"");

            try {
                return GameServer.INSTANCE.getGameUniverse().getUser(username).getControlledUnit().getFloppyData().getBytes();
            } catch (Exception e) {
                String message = "Encountered exception while reading floppy data: " + e.getMessage();

                LogManager.LOGGER.severe(message);
                return message;
            }

        } else {
            response.status(403);
            return "Not logged in";
        }

    }
}
