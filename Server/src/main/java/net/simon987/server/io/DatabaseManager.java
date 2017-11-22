package net.simon987.server.io;


import net.simon987.server.ServerConfiguration;
import net.simon987.server.logging.LogManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the database: this class manages the interactions with the database
 */
public abstract class DatabaseManager {

    /**
     * SQL connection url
     */
    private String url;

    /**
     * SQL username
     */
    private String username;

    /**
     * SQL password
     */
    private String password;

    public DatabaseManager(ServerConfiguration config) {
        this.url = config.getString("mysql_url");
        this.username = config.getString("mysql_user");
        this.password = config.getString("mysql_pass");
    }

    protected Connection getConnection() {
        try {
            return DriverManager.getConnection(url, this.username, password);
        } catch (SQLException e) {
            LogManager.LOGGER.severe("Couldn't connect to MySQL server state:" + e.getSQLState() + " error:" + e.getErrorCode());
            return null;
        }
    }
}
