package net.simon987.server;


import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventDispatcher;
import net.simon987.server.event.TickEvent;
import net.simon987.server.game.DayNightCycle;
import net.simon987.server.game.GameUniverse;
import net.simon987.server.game.World;
import net.simon987.server.io.FileUtils;
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
import java.util.ArrayList;

public class GameServer implements Runnable {

    public final static GameServer INSTANCE = new GameServer();
	private final static String SAVE_JSON = "save.json";
    
	private GameUniverse gameUniverse;
    private GameEventDispatcher eventDispatcher;
    private PluginManager pluginManager;

    private ServerConfiguration config;

    private SocketServer socketServer;

    private int maxExecutionTime;

    private DayNightCycle dayNightCycle;

    public GameServer() {

        this.config = new ServerConfiguration(new File("config.properties"));

        gameUniverse = new GameUniverse(config);
        pluginManager = new PluginManager();

        maxExecutionTime = config.getInt("user_timeout");


        dayNightCycle = new DayNightCycle();

        //Load all plugins in plugins folder, if it doesn't exist, create it
        File pluginDir = new File("plugins/");
        File[] pluginDirListing = pluginDir.listFiles();

        if (pluginDirListing != null) {
            for (File pluginFile : pluginDirListing) {

                if (pluginFile.getName().endsWith(".jar")) {
                    pluginManager.load(pluginFile, config);
                }

            }
        } else {
            if (!pluginDir.mkdir()) {
                LogManager.LOGGER.severe("Couldn't create plugin directory");
            }
        }

        eventDispatcher = new GameEventDispatcher(pluginManager);
        eventDispatcher.getListeners().add(dayNightCycle);

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

        //Dispatch tick event
        GameEvent event = new TickEvent(gameUniverse.getTime());
        eventDispatcher.dispatch(event); //Ignore cancellation


        //Process user code
        ArrayList<User> users_ = gameUniverse.getUsers();
        for (User user : users_) {

            if (user.getCpu() != null) {
                try {

                    int timeout = Math.min(user.getControlledUnit().getEnergy(), maxExecutionTime);

                    user.getCpu().reset();
                    int cost = user.getCpu().execute(timeout);
                    user.getControlledUnit().spendEnergy(cost);

                } catch (Exception e) {
                    LogManager.LOGGER.severe("Error executing " + user.getUsername() + "'s code");
                    e.printStackTrace();
                }

            }
        }

        //Process each worlds
        //Avoid concurrent modification
        ArrayList<World> worlds = new ArrayList<>(gameUniverse.getWorlds());
        for (World world : worlds) {
            world.update();
        }

        //Save
        if (gameUniverse.getTime() % config.getInt("save_interval") == 0) {
            save(new File("save.json"));
        }
        
		// Clean up history files
		if(gameUniverse.getTime() % config.getInt("clean_interval") == 0) {
			FileUtils.cleanHistory(config.getInt("history_size"));
		}

        socketServer.tick();

        LogManager.LOGGER.info("Processed " + gameUniverse.getWorlds().size() + " worlds");
    }

    /**
     * Save game universe to file in JSON format
     *
     * @param file JSON file to save
     */
    public void save(File file) {

		boolean dirExists = FileUtils.prepDirectory(FileUtils.DIR_PATH);
		
		if (new File(new File(SAVE_JSON).getAbsolutePath()).exists() && dirExists) {
			byte[] data = FileUtils.bytifyFile(new File(SAVE_JSON).toPath());
			try {
				FileUtils.writeSaveToZip(SAVE_JSON, data);
			} catch (IOException e) {
				System.out.println("Failed to write " + SAVE_JSON + " to zip file");
				e.printStackTrace();
			}
		}
		
        try {
            FileWriter fileWriter = new FileWriter(file);

            JSONObject universe = gameUniverse.serialise();

            JSONArray plugins = new JSONArray();

            for (ServerPlugin plugin : pluginManager.getPlugins()) {
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

    public DayNightCycle getDayNightCycle() {
        return dayNightCycle;
    }
}
