package net.simon987.server;


import net.simon987.server.logging.LogManager;

import java.io.*;
import java.util.Properties;

/**
 * Wrapper for Java Properties class
 */
public class ServerConfiguration {

    private Properties properties;
    private String fileName;

    public ServerConfiguration(String fileName) {

        this.fileName = fileName;

        try {
            properties = new Properties();
            InputStream is = new FileInputStream(this.fileName);
            properties.load(is);

        } catch (IOException e) {
            LogManager.LOGGER.severe("Problem loading server configuration: " + e.getMessage());
        }
    }

    private void saveConfig() {

        try {
            OutputStream os = new FileOutputStream(this.fileName);
            properties.store(os, "");

        } catch (IOException e) {
            LogManager.LOGGER.severe("Problem saving server configuration: " + e.getMessage());
        }
    }

    public int getInt(String key) {
        return Integer.valueOf((String) properties.get(key));

    }

    public String getString(String key) {

        return (String) properties.get(key);
    }

    public void setInt(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
        saveConfig();
    }

    public void setString(String key, String value) {
        properties.setProperty(key, value);
        saveConfig();
    }

}
