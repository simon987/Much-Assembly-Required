package net.simon987.server;


import net.simon987.server.event.GameEventDispatcher;
import net.simon987.server.game.GameUniverse;
import net.simon987.server.game.World;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.PluginManager;
import net.simon987.server.plugin.ServerPlugin;
import net.simon987.server.user.User;
import net.simon987.server.webserver.SocketServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GameServer implements Runnable {

    public final static GameServer INSTANCE = new GameServer();

    private GameUniverse gameUniverse;
    private GameEventDispatcher eventDispatcher;
    private PluginManager pluginManager;

    private ServerConfiguration config;

    private SocketServer socketServer;

    public GameServer() {

        this.config = new ServerConfiguration(new File("config.properties"));

        gameUniverse = new GameUniverse(config);
        pluginManager = new PluginManager();

        //Load all plugins in plugins folder, if it doesn't exist, create it
        File pluginDir = new File("plugins/");
        File[] pluginDirListing = pluginDir.listFiles();

        if(pluginDirListing != null) {
            for(File pluginFile : pluginDirListing) {

                if(pluginFile.getName().endsWith(".jar")){
                    pluginManager.load(pluginFile);
                }

            }
        } else {
            if(!pluginDir.mkdir()) {
                LogManager.LOGGER.severe("Couldn't create plugin directory");
            }
        }

        eventDispatcher = new GameEventDispatcher(pluginManager);

    }

    public GameUniverse getGameUniverse() {
        return gameUniverse;
    }

    public GameEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Override
    public void run() {
        LogManager.LOGGER.info("(G) Started game loop");

        long startTime; //Start time of the loop
        long uTime;     //update time
        long waitTime;  //time to wait

        boolean running = true;

        while (running) {

            startTime = System.currentTimeMillis();

            tick();

            uTime = System.currentTimeMillis() - startTime;
            waitTime = config.getInt("tick_length") - uTime;

            LogManager.LOGGER.info("Wait time : " + waitTime + "ms | Update time: " + uTime + "ms | " + (int) (((double) uTime / waitTime) * 100) + "% load");

            try {
                if (waitTime >= 0) {
                    Thread.sleep(waitTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }

    private void tick() {
        gameUniverse.incrementTime();

        //Process user code
        for(User user : gameUniverse.getUsers()){
            user.getCpu().reset();
            user.getCpu().execute();

//            System.out.println(user.getCpu());
        }

        //Process each worlds
        for (World world : gameUniverse.getWorlds()) {
           world.update();
        }

        socketServer.tick();

        LogManager.LOGGER.info("Processed " + gameUniverse.getWorlds().size() + " worlds");
    }
    /**
     * Save game universe to file in JSON format
     * @param file JSON file to save
     */
    public void save(File file){

        try {
            FileWriter fileWriter = new FileWriter(file);

            JSONObject universe = gameUniverse.serialise();

            JSONArray plugins = new JSONArray();

            for(ServerPlugin plugin : pluginManager.getPlugins()){
                plugins.add(plugin.serialise());
            }

            universe.put("plugins", plugins);

            fileWriter.write(universe.toJSONString());
            fileWriter.close();

            LogManager.LOGGER.info("Saved to file " + file.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ServerConfiguration getConfig() {
        return config;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public void setSocketServer(SocketServer socketServer) {
        this.socketServer = socketServer;
    }
}
