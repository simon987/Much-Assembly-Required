package net.simon987.server;


import net.simon987.server.logging.LogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Wrapper for Java Property
 */
public class ServerConfiguration {

    /**
     * Properties
     */
    private Properties properties;

    public ServerConfiguration(File file) {
        try {
            properties = new Properties();

            properties.load(new FileInputStream(file));

        } catch (IOException e) {
            LogManager.LOGGER.severe("Problem loading server configuration: " + e.getMessage());
        }
    }

    public int getInt(String key) {
        return Integer.valueOf((String) properties.get(key));

    }

    public String getString(String key) {

        return (String) properties.get(key);
    }

}
