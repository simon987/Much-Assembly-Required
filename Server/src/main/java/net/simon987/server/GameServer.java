package net.simon987.server;


import com.mongodb.*;
import net.simon987.server.assembly.exception.CancelledException;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventDispatcher;
import net.simon987.server.event.TickEvent;
import net.simon987.server.game.DayNightCycle;
import net.simon987.server.game.GameUniverse;
import net.simon987.server.game.World;
import net.simon987.server.io.FileUtils;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.PluginManager;
import net.simon987.server.user.User;
import net.simon987.server.webserver.SocketServer;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class GameServer implements Runnable {

    public final static GameServer INSTANCE = new GameServer();

    private GameUniverse gameUniverse;
    private GameEventDispatcher eventDispatcher;
    private PluginManager pluginManager;

    private ServerConfiguration config;

    private SocketServer socketServer;

    private int maxExecutionTime;

    private DayNightCycle dayNightCycle;

    public GameServer() {

        this.config = new ServerConfiguration("config.properties");

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
        ArrayList<User> users_ = new ArrayList<>(gameUniverse.getUsers());
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
        int updatedWorlds = 0;
        for (World world : worlds) {
            if (world.shouldUpdate()) {
                world.update();
                updatedWorlds++;
            }
        }

        //Save
        if (gameUniverse.getTime() % config.getInt("save_interval") == 0) {
            save();
        }

        // Clean up history files
        if (gameUniverse.getTime() % config.getInt("clean_interval") == 0) {
            FileUtils.cleanHistory(config.getInt("history_size"));
        }

        socketServer.tick();

        LogManager.LOGGER.info("Processed " + gameUniverse.getWorlds().size() + " worlds (" + updatedWorlds +
                ") updated");
    }

    void load() {

        LogManager.LOGGER.info("Loading from MongoDB");
        MongoClient mongo;
        try {
            mongo = new MongoClient("localhost", 27017);

            DB db = mongo.getDB("mar");

            DBCollection worlds = db.getCollection("world");
            DBCollection users = db.getCollection("user");
            DBCollection server = db.getCollection("server");

            //Load worlds
            DBCursor cursor = worlds.find();
            while (cursor.hasNext()) {
                GameServer.INSTANCE.getGameUniverse().getWorlds().add(World.deserialize(cursor.next()));
            }

            //Load users
            cursor = users.find();
            while (cursor.hasNext()) {
                try {
                    GameServer.INSTANCE.getGameUniverse().getUsers().add(User.deserialize(cursor.next()));
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

            LogManager.LOGGER.info("Done loading! W:" + GameServer.INSTANCE.getGameUniverse().getWorlds().size() +
                    " | U:" + GameServer.INSTANCE.getGameUniverse().getUsers().size());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void save() {

        LogManager.LOGGER.info("Saving to MongoDB | W:" + gameUniverse.getWorlds().size() + " | U:" + gameUniverse.getUsers().size());

        MongoClient mongo;
        try {
            mongo = new MongoClient("localhost", 27017);

            DB db = mongo.getDB("mar");

            db.dropDatabase(); //Todo: Update database / keep history instead of overwriting

            DBCollection worlds = db.getCollection("world");
            DBCollection users = db.getCollection("user");
            DBCollection server = db.getCollection("server");

            List<DBObject> worldDocuments = new ArrayList<>();
            int perBatch = 35;
            int insertedWorlds = 0;
            ArrayList<World> worlds_ = new ArrayList<>(GameServer.INSTANCE.getGameUniverse().getWorlds());
            for (World w : worlds_) {
                worldDocuments.add(w.mongoSerialise());
                insertedWorlds++;

                if (worldDocuments.size() >= perBatch || insertedWorlds >= GameServer.INSTANCE.getGameUniverse().getWorlds().size()) {
                    worlds.insert(worldDocuments);
                    worldDocuments.clear();
                }
            }

            List<DBObject> userDocuments = new ArrayList<>();
            int insertedUsers = 0;
            ArrayList<User> users_ = new ArrayList<>(GameServer.INSTANCE.getGameUniverse().getUsers());
            for (User u : users_) {

                insertedUsers++;

                if (!u.isGuest()) {
                    userDocuments.add(u.mongoSerialise());
                }

                if (userDocuments.size() >= perBatch || insertedUsers >= GameServer.INSTANCE.getGameUniverse().getUsers().size()) {
                    users.insert(userDocuments);
                    userDocuments.clear();
                }
            }

            BasicDBObject serverObj = new BasicDBObject();
            serverObj.put("time", gameUniverse.getTime());
            serverObj.put("nextObjectId", gameUniverse.getNextObjectId());
            server.insert(serverObj);

            LogManager.LOGGER.info("Done!");
        } catch (UnknownHostException e) {
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
