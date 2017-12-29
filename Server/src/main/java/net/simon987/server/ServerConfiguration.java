package net.simon987.server;


import net.simon987.server.logging.LogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Wrapper for Java Property
 */
public class ServerConfiguration {

    /**
     * Properties
     */
    private Properties properties;

    public ServerConfiguration(String file) {
        try {
            properties = new Properties();
            InputStream is = new FileInputStream("config.properties");
            properties.load(is);

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
