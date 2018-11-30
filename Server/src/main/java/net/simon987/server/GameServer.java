package net.simon987.server;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import net.simon987.server.crypto.CryptoProvider;
import net.simon987.server.crypto.SecretKeyGenerator;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventDispatcher;
import net.simon987.server.event.TickEvent;
import net.simon987.server.game.GameUniverse;
import net.simon987.server.game.debug.*;
import net.simon987.server.game.item.ItemCopper;
import net.simon987.server.game.item.ItemIron;
import net.simon987.server.game.objects.GameRegistry;
import net.simon987.server.game.world.*;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.PluginManager;
import net.simon987.server.user.User;
import net.simon987.server.user.UserManager;
import net.simon987.server.user.UserStatsHelper;
import net.simon987.server.websocket.SocketServer;
import org.bson.Document;

import java.util.ArrayList;

public class GameServer implements Runnable {

    public final static GameServer INSTANCE = new GameServer();

    private GameUniverse gameUniverse;
    private GameEventDispatcher eventDispatcher;
    private PluginManager pluginManager;

    private ServerConfiguration config;

    private SocketServer socketServer;

    private int maxExecutionTime;

    private DayNightCycle dayNightCycle;

    private CryptoProvider cryptoProvider;

    private MongoClient mongo;

    private UserManager userManager;

    private UserStatsHelper userStatsHelper;

    private GameRegistry gameRegistry;

    private String secretKey;

    public GameServer() {
        this.config = new ServerConfiguration("config.properties");

        mongo = new MongoClient(config.getString("mongo_address"), config.getInt("mongo_port"));
        MongoDatabase db = mongo.getDatabase(config.getString("mongo_dbname"));

        MongoCollection<Document> userCollection = db.getCollection("user");

        userManager = new UserManager(userCollection);
        userStatsHelper = new UserStatsHelper(userCollection);

        gameUniverse = new GameUniverse(config);
        gameUniverse.setMongo(mongo);
        gameRegistry = new GameRegistry();
        pluginManager = new PluginManager(config, gameRegistry);

        maxExecutionTime = config.getInt("user_timeout");

        cryptoProvider = new CryptoProvider();

        dayNightCycle = new DayNightCycle();

        SecretKeyGenerator keyGenerator = new SecretKeyGenerator();
        secretKey = config.getString("secret_key");
        if (secretKey == null) {
            secretKey = keyGenerator.generate();
            config.setString("secret_key", secretKey);
        }

        if (!pluginManager.loadInFolder("plugins/")) {
            System.exit(-1);
        }

        eventDispatcher = new GameEventDispatcher(pluginManager);
        eventDispatcher.getListeners().add(dayNightCycle);

        //Debug command Listeners
        eventDispatcher.getListeners().add(new ComPortMsgCommandListener());
        eventDispatcher.getListeners().add(new CreateWorldCommandListener());
        eventDispatcher.getListeners().add(new KillAllCommandListener());
        eventDispatcher.getListeners().add(new MoveObjCommandListener());
        eventDispatcher.getListeners().add(new ObjInfoCommandListener());
        eventDispatcher.getListeners().add(new SetTileAtCommandListener());
        eventDispatcher.getListeners().add(new SpawnObjCommandListener());
        eventDispatcher.getListeners().add(new TpObjectCommandListener());
        eventDispatcher.getListeners().add(new UserInfoCommandListener());
        eventDispatcher.getListeners().add(new HealObjCommandListener());
        eventDispatcher.getListeners().add(new DamageObjCommandListener());
        eventDispatcher.getListeners().add(new SetEnergyCommandListener());
        eventDispatcher.getListeners().add(new SaveGameCommandListener());

        gameRegistry.registerItem(ItemCopper.ID, ItemCopper.class);
        gameRegistry.registerItem(ItemIron.ID, ItemIron.class);

        gameRegistry.registerTile(TileVoid.ID, TileVoid.class);
        gameRegistry.registerTile(TilePlain.ID, TilePlain.class);
        gameRegistry.registerTile(TileWall.ID, TileWall.class);
        gameRegistry.registerTile(TileCopper.ID, TileCopper.class);
        gameRegistry.registerTile(TileIron.ID, TileIron.class);
    }

    public GameUniverse getGameUniverse() {
        return gameUniverse;
    }

    public GameEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public CryptoProvider getCryptoProvider() {
        return cryptoProvider;
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

            if (user.getControlledUnit() != null && user.getControlledUnit().getCpu() != null) {
                try {

                    int timeout = Math.min(user.getControlledUnit().getEnergy(), maxExecutionTime);

                    user.getControlledUnit().getCpu().reset();
                    int cost = user.getControlledUnit().getCpu().execute(timeout);
                    user.getControlledUnit().spendEnergy(cost);
                } catch (Exception e) {
                    LogManager.LOGGER.severe("Error executing " + user.getUsername() + "'s code");
                    e.printStackTrace();
                }
            }
        }

        //Process each worlds
        for (World world : gameUniverse.getWorlds()) {
            if (world.shouldUpdate()) {
                world.update();
            }
        }

        //Save
        if (gameUniverse.getTime() % config.getInt("save_interval") == 0) {
            save();
        }

        socketServer.tick();
    }

    void load() {

        LogManager.LOGGER.info("Loading all data from MongoDB");

        MongoDatabase db = mongo.getDatabase(config.getString("mongo_dbname"));

        MongoCollection<Document> worlds = db.getCollection("world");
        MongoCollection<Document> server = db.getCollection("server");

        Document whereQuery = new Document();
        whereQuery.put("shouldUpdate", true);
        MongoCursor<Document> cursor = worlds.find(whereQuery).iterator();
        GameUniverse universe = GameServer.INSTANCE.getGameUniverse();
        while (cursor.hasNext()) {
            World w = World.deserialize(cursor.next());
            universe.addWorld(w);
        }

        //Load users
        ArrayList<User> userList = userManager.getUsers();
        for (User user : userList) {
            universe.addUser(user);
        }

        //Load misc server info
        cursor = server.find().iterator();
        if (cursor.hasNext()) {
            Document serverObj = cursor.next();
            gameUniverse.setTime((long) serverObj.get("time"));
        }

        LogManager.LOGGER.info("Done loading! W:" + GameServer.INSTANCE.getGameUniverse().getWorldCount() +
                " | U:" + GameServer.INSTANCE.getGameUniverse().getUserCount());
    }

    public void save() {

        LogManager.LOGGER.info("Saving to MongoDB | W:" + gameUniverse.getWorldCount() + " | U:" + gameUniverse.getUserCount());
        try {
            MongoDatabase db = mongo.getDatabase(config.getString("mongo_dbname"));
            ReplaceOptions updateOptions = new ReplaceOptions();
            updateOptions.upsert(true);

            int unloaded_worlds = 0;

            MongoCollection<Document> worlds = db.getCollection("world");
            MongoCollection<Document> users = db.getCollection("user");
            MongoCollection<Document> server = db.getCollection("server");

            int insertedWorlds = 0;
            GameUniverse universe = GameServer.INSTANCE.getGameUniverse();
            for (World w : universe.getWorlds()) {
                insertedWorlds++;
                worlds.replaceOne(new Document("_id", w.getId()), w.mongoSerialise(), updateOptions);

                //If the world should unload, it is removed from the Universe after having been saved.
                if (w.shouldUnload()) {
                    unloaded_worlds++;
                    universe.removeWorld(w);
                }
            }

            for (User u : GameServer.INSTANCE.getGameUniverse().getUsers()) {
                if (!u.isGuest()) {
                    users.replaceOne(new Document("_id", u.getUsername()), u.mongoSerialise(), updateOptions);
                }
            }

            Document serverObj = new Document();
            serverObj.put("time", gameUniverse.getTime());
            //A constant id ensures only one entry is kept and updated, instead of a new entry created every save.
            server.replaceOne(new Document("_id", "serverinfo"), serverObj, updateOptions);

            LogManager.LOGGER.info("" + insertedWorlds + " worlds saved, " + unloaded_worlds + " unloaded");
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

    public UserManager getUserManager() {
        return userManager;
    }

    public UserStatsHelper getUserStatsHelper() {
        return userStatsHelper;
    }

    public GameRegistry getRegistry() {
        return gameRegistry;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        config.setString("secret_key", secretKey);
    }
}
