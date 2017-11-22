package net.simon987.server.logging;

import java.io.IOException;
import java.util.logging.*;

/**
 * Utility class to manage log entries
 */
public class LogManager {

    /**
     * Singleton Logger
     */
    public final static Logger LOGGER = Logger.getLogger("mar");

    /**
     * Initialises the logger
     */
    public static void initialize() {
        LOGGER.setUseParentHandlers(false);

        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new GenericFormatter());

        try {
            Handler fileHandler = new FileHandler("mar.log");
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new GenericFormatter());

            LOGGER.addHandler(handler);
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
