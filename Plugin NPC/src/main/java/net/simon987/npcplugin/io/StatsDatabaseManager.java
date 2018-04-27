package net.simon987.npcplugin.io;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.ControllableUnit;
import net.simon987.server.io.DatabaseManager;

public class StatsDatabaseManager extends DatabaseManager {

    public StatsDatabaseManager(ServerConfiguration config) {
        super(config);
    }

    public void saveVaultCompletion(ControllableUnit unit, String dimension) {



    }
}
