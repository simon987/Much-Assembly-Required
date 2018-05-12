package net.simon987.server.web;

import net.simon987.server.GameServer;
import net.simon987.server.logging.LogManager;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;

public class FloppyUploadRoute implements Route {

    @Override
    public Object handle(Request request, Response response) {

        String username = request.session().attribute("username");

        if (username != null) {
            try {
                request.attribute("org.eclipse.jetty.multipartConfig",
                        new MultipartConfigElement("/tmp_floppy", 1474560L, -1L, 0));

                try (InputStream is = request.raw().getPart("floppyData").getInputStream()) {

                    if (is.available() == 1474560) {

                        byte[] bytes = new byte[1474560];
                        int bytesRead = is.read(bytes);

                        if (bytesRead == 1474560) {

                            try {
                                GameServer.INSTANCE.getGameUniverse().getUser(username).getControlledUnit().getFloppyData().setBytes(bytes);
                                return "ok";
                            } catch (Exception e) {
                                String message = "Encountered exception while writing floppy data: " + e.getMessage();

                                LogManager.LOGGER.severe(message);
                                return message;
                            }

                        } else {
                            return "Couldn't read floppy file";
                        }

                    } else {
                        return "File size must be 1474560 bytes";
                    }

                } catch (IOException | ServletException e) {
                    e.printStackTrace();
                    return "Error reading floppy file: " + e.getMessage();
                }

            } catch (IllegalStateException e) {
                return "File exceeds maximum size";
            }
        } else {
            response.status(403);
            return "Not logged in";
        }

    }
}
