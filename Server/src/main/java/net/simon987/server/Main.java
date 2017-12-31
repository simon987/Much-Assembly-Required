package net.simon987.server;

import net.simon987.server.logging.LogManager;
import net.simon987.server.webserver.SocketServer;

import java.io.File;
import java.net.InetSocketAddress;


public class Main {
    public static void main(String[] args) {


        LogManager.initialize();
        ServerConfiguration config = new ServerConfiguration("config.properties");
        //Load
        GameServer.INSTANCE.getGameUniverse().load(new File("save.json"));


        SocketServer socketServer = new SocketServer(new InetSocketAddress(config.getString("webSocket_host"),
                config.getInt("webSocket_port")), config);

        GameServer.INSTANCE.setSocketServer(socketServer);

        System.out.println(GameServer.INSTANCE.getGameUniverse().getWorld(0x7fff, 0x7fff));

        (new Thread(socketServer)).start();
        (new Thread(GameServer.INSTANCE)).start();
    }
}
