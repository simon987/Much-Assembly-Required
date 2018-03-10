package net.simon987.server.logging;

import net.simon987.server.ServerConfiguration;

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
    public static void initialize(ServerConfiguration config) {
        LOGGER.setUseParentHandlers(false);

        /*
        * Having warning/error directed to stderr
        */
        Handler errHandler = new ConsoleHandler();
        errHandler.setFormatter(new GenericFormatter());
        errHandler.setLevel(Level.WARNING);

        /*
        * Only have info and below directed to stdout
        */
        Handler handler = new StreamHandler(System.out, new GenericFormatter()) {
            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        handler.setFilter(new Filter() {
            @Override
            public boolean isLoggable(LogRecord record) {
                return record.getLevel().intValue() <= Level.INFO.intValue();
            }
        });
        handler.setLevel(Level.ALL);

        try {
            Handler fileHandler = new FileHandler("mar-%g.log", config.getInt("log_limit"),
                    config.getInt("log_count"));
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new GenericFormatter());


            LOGGER.addHandler(handler);
            LOGGER.addHandler(errHandler);
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
