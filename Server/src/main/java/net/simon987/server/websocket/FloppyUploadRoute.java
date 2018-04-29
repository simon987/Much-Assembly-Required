package net.simon987.server.websocket;

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

        try {
            request.attribute("org.eclipse.jetty.multipartConfig",
                    new MultipartConfigElement("/tmp_floppy", 1474560L, -1L, 0));

            try (InputStream is = request.raw().getPart("floppyData").getInputStream()) {

                System.out.println(is.available());

                // Use the input stream to create a file
            } catch (IOException | ServletException e) {
                e.printStackTrace();
            }

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return "File exceeds maximum size";
        }

        return "mh";
    }
}
