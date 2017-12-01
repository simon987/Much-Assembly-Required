package net.simon987.server;

import net.simon987.server.logging.LogManager;
import net.simon987.server.webserver.SocketServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;


public class Main {
    public static void main(String[] args) {

    	//Writes all of the files stored in GameServer.saveArray to a zip file.
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
					ZipUtils.writeSavesToZip(GameServer.INSTANCE.getSaveArchive());
				} catch (IOException e) {
					System.out.println("Error writing saves to zip");
					e.printStackTrace();
				}
            }
        }, "Shutdown-thread"));

        LogManager.initialize();
        ServerConfiguration config = new ServerConfiguration(new File("config.properties"));

        //Load
        GameServer.INSTANCE.getGameUniverse().load(new File("save.json"));

        SocketServer socketServer = new SocketServer(new InetSocketAddress(config.getString("webSocket_host"),
                config.getInt("webSocket_port")), config);

        GameServer.INSTANCE.setSocketServer(socketServer);


        (new Thread(socketServer)).start();
        (new Thread(GameServer.INSTANCE)).start();
    }
}
