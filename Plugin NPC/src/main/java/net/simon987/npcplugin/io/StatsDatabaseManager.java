package net.simon987.npcplugin.io;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.ControllableUnit;
import net.simon987.server.io.DatabaseManager;
import net.simon987.server.logging.LogManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatsDatabaseManager extends DatabaseManager {

    public StatsDatabaseManager(ServerConfiguration config) {
        super(config);
    }

    public void saveVaultCompletion(ControllableUnit unit, String dimension) {

        Connection connection = getConnection();

        try {

            PreparedStatement p = connection.prepareStatement("INSERT INTO mar_vault_clear " +
                    "(username, clear_time, vault_id) VALUES (?,?,?)");
            p.setString(1, unit.getParent().getUsername());
            p.setInt(2, 0);
            p.setString(3, dimension);

            int result = p.executeUpdate();

            LogManager.LOGGER.fine("Saved vault clear (" + result + " rows changed)");

        } catch (MySQLIntegrityConstraintViolationException e) {
            LogManager.LOGGER.fine("This vault was already cleared by " + unit.getParent().getUsername());
        } catch (SQLException e) {
            LogManager.LOGGER.severe(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
