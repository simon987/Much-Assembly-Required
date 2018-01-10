package net.simon987.server;


import com.mongodb.*;
import net.simon987.server.assembly.exception.CancelledException;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventDispatcher;
import net.simon987.server.event.TickEvent;
import net.simon987.server.game.DayNightCycle;
import net.simon987.server.game.GameUniverse;
import net.simon987.server.game.World;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.PluginManager;
import net.simon987.server.user.User;
import net.simon987.server.webserver.SocketServer;

import java.io.File;
import java.net.UnknownHostException;

public class GameServer implements Runnable {

    public final static GameServer INSTANCE = new GameServer();

    private GameUniverse gameUniverse;
    private GameEventDispatcher eventDispatcher;
    private PluginManager pluginManager;

    private ServerConfiguration config;

    private SocketServer socketServer;

    private int maxExecutionTime;

    private DayNightCycle dayNightCycle;

	private MongoClient mongo = null;

    public GameServer() {

        this.config = new ServerConfiguration("config.properties");

        try{
	        mongo = new MongoClient("localhost", 27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        gameUniverse = new GameUniverse(config);
        gameUniverse.setMongo(mongo);
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
        for (User user : gameUniverse.getUsers()) {

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
        int updatedWorlds = 0;
        for (World world : gameUniverse.getWorlds()) {
            if (world.shouldUpdate()) {
                world.update();
                updatedWorlds++;
            }
        }

        //Save
        if (gameUniverse.getTime() % config.getInt("save_interval") == 0) {
            save();
        }

        socketServer.tick();

        LogManager.LOGGER.info("Processed " + gameUniverse.getWorldCount() + " worlds (" + updatedWorlds +
                ") updated");
    }



    void load() {

        LogManager.LOGGER.info("Loading all data from MongoDB");

        DB db = mongo.getDB("mar");

        DBCollection worlds = db.getCollection("world");
        DBCollection users = db.getCollection("user");
        DBCollection server = db.getCollection("server");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("shouldUpdate", true);
        DBCursor cursor = worlds.find(whereQuery);
        GameUniverse universe = GameServer.INSTANCE.getGameUniverse();
        while (cursor.hasNext()) {
        	World w = World.deserialize(cursor.next());
            universe.addWorld(w);
        }

        //Load users
        cursor = users.find();
        while (cursor.hasNext()) {
            try {
                universe.addUser(User.deserialize(cursor.next()));
            } catch (CancelledException e) {
                e.printStackTrace();
            }
        }

        //Load misc server info
        cursor = server.find();
        if (cursor.hasNext()) {
            DBObject serverObj = cursor.next();
            gameUniverse.setTime((long) serverObj.get("time"));
            gameUniverse.setNextObjectId((long) serverObj.get("nextObjectId"));
        }

        LogManager.LOGGER.info("Done loading! W:" + GameServer.INSTANCE.getGameUniverse().getWorldCount() +
                " | U:" + GameServer.INSTANCE.getGameUniverse().getUserCount());
    }

    private void save() {

        LogManager.LOGGER.info("Saving to MongoDB | W:" + gameUniverse.getWorldCount() + " | U:" + gameUniverse.getUserCount());
        try{
	        DB db = mongo.getDB("mar");

	        int unloaded_worlds = 0;

	        DBCollection worlds = db.getCollection("world");
	        DBCollection users = db.getCollection("user");
	        DBCollection server = db.getCollection("server");

	        int insertedWorlds = 0;
	        GameUniverse universe = GameServer.INSTANCE.getGameUniverse();
            for (World w : universe.getWorlds()) {
//	            LogManager.LOGGER.fine("Saving world "+w.getId()+" to mongodb");
                insertedWorlds++;
	            worlds.save(w.mongoSerialise());
	                
	         	// If the world should unload, it is removed from the Universe after having been saved. 
	        	if (w.shouldUnload()){
	        		unloaded_worlds++;
//				 	LogManager.LOGGER.fine("Unloading world "+w.getId()+" from universe");
                    universe.removeWorld(w);
	        	}
	        }

            for (User u : GameServer.INSTANCE.getGameUniverse().getUsers()) {

	            if (!u.isGuest()) {
	            	users.save(u.mongoSerialise());
	            }

	        }

	        BasicDBObject serverObj = new BasicDBObject();
	        serverObj.put("_id","serverinfo"); // a constant id ensures only one entry is kept and updated, instead of a new entry created every save.
	        serverObj.put("time", gameUniverse.getTime());
	        serverObj.put("nextObjectId", gameUniverse.getNextObjectId());
			server.save(serverObj);

			LogManager.LOGGER.info(""+insertedWorlds+" worlds saved, "+unloaded_worlds+" unloaded");
	        LogManager.LOGGER.info("Done!");
        } catch (Exception e) {
            LogManager.LOGGER.severe("Problem happened during save function");
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
