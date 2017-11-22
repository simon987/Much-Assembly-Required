package net.simon987.server.webserver;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.io.DatabaseManager;
import net.simon987.server.logging.LogManager;

import java.sql.*;

class SocketServerDatabase extends DatabaseManager {

    public SocketServerDatabase(ServerConfiguration config) {
        super(config);
    }

    String validateAuthToken(String token) {

        Connection connection = null;
        try {
            connection = getConnection();

            PreparedStatement p = connection.prepareStatement("SELECT username FROM mar_user WHERE authToken=?");
            p.setString(1, token);

            ResultSet rs = p.executeQuery();

            if (rs.next()) {

                return rs.getString("username");

            } else {
                return null;
            }

        } catch (SQLException e) {
            LogManager.LOGGER.severe("MySQL Error " + e.getErrorCode() + ": " + e.getMessage());

        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    byte[] getFloppy(String username) {

        Connection connection = null;
        try {
            connection = getConnection();

            PreparedStatement p = connection.prepareStatement("SELECT floppyData FROM mar_user WHERE username=?");
            p.setString(1, username);

            ResultSet rs = p.executeQuery();

            if (rs.next()) {
                Blob blob = rs.getBlob("floppyData");

                if (blob != null) {
                    return blob.getBytes(1, (int) blob.length() - 1);
                }
            }

        } catch (SQLException e) {
            LogManager.LOGGER.severe("MySQL Error " + e.getErrorCode() + ": " + e.getMessage());

        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;

    }


}
