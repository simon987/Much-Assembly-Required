package net.simon987.server;

public interface IServerConfiguration {

    int getInt(String key);

    String getString(String key);

    void setInt(String key, int value);

    void setString(String key, String value);
}
