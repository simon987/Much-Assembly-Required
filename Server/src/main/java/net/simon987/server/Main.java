package net.simon987.server;

import net.simon987.server.logging.LogManager;
import net.simon987.server.web.WebServer;


public class Main {
    public static void main(String[] args) {

        ServerConfiguration config = new ServerConfiguration("config.properties");
        LogManager.initialize(config);

        //Load
        GameServer.INSTANCE.load();
        (new Thread(GameServer.INSTANCE)).start();

        //Web server
        new WebServer(GameServer.INSTANCE.getConfig());
    }

}
