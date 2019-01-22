package net.simon987.server;

import java.util.Properties;

public class FakeConfiguration implements IServerConfiguration {

    private Properties properties;


    public FakeConfiguration() {
        this.properties = new Properties();
    }

    @Override
    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    @Override
    public String getString(String key) {
        return properties.getProperty(key);
    }

    @Override
    public void setInt(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
    }

    @Override
    public void setString(String key, String value) {
        properties.setProperty(key, value);
    }
}
