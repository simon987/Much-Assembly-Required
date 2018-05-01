package net.simon987.server;

import net.simon987.server.logging.LogManager;
import net.simon987.server.web.WebServer;
import spark.Spark;


public class Main {
    public static void main(String[] args) {

        ServerConfiguration config = new ServerConfiguration("config.properties");
        LogManager.initialize(config);

        GameServer.INSTANCE.load();

        //Web server
        WebServer webServer = new WebServer(GameServer.INSTANCE.getConfig());

        Spark.awaitInitialization();
        GameServer.INSTANCE.setSocketServer(webServer.getSocketServer());

        (new Thread(GameServer.INSTANCE)).start();
    }
}
