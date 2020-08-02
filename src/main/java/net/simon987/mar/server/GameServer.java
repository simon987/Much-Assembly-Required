package net.simon987.mar.server;

import com.mongodb.MongoClientException;
import com.mongodb.client.*;
import com.mongodb.client.model.ReplaceOptions;
import net.simon987.mar.biomass.BiomassBlob;
import net.simon987.mar.biomass.ItemBiomass;
import net.simon987.mar.biomass.event.ObjectDeathListener;
import net.simon987.mar.biomass.event.WorldCreationListener;
import net.simon987.mar.biomass.event.WorldUpdateListener;
import net.simon987.mar.construction.ConstructionSite;
import net.simon987.mar.construction.ItemBluePrint;
import net.simon987.mar.construction.Obstacle;
import net.simon987.mar.cubot.*;
import net.simon987.mar.cubot.event.*;
import net.simon987.mar.cubot.Clock;
import net.simon987.mar.cubot.RandomNumberGenerator;
import net.simon987.mar.npc.*;
import net.simon987.mar.npc.event.BeforeSaveListener;
import net.simon987.mar.npc.event.LoadListener;
import net.simon987.mar.npc.event.VaultCompleteListener;
import net.simon987.mar.npc.event.VaultWorldUpdateListener;
import net.simon987.mar.npc.world.TileVaultFloor;
import net.simon987.mar.npc.world.TileVaultWall;
import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.crypto.CryptoProvider;
import net.simon987.mar.server.crypto.SecretKeyGenerator;
import net.simon987.mar.server.event.*;
import net.simon987.mar.server.game.GameUniverse;
import net.simon987.mar.server.game.debug.*;
import net.simon987.mar.server.game.item.ItemCopper;
import net.simon987.mar.server.game.item.ItemIron;
import net.simon987.mar.server.game.objects.GameRegistry;
import net.simon987.mar.server.game.world.*;
import net.simon987.mar.server.logging.LogManager;
import net.simon987.mar.server.user.User;
import net.simon987.mar.server.user.UserManager;
import net.simon987.mar.server.user.UserStatsHelper;
import net.simon987.mar.server.websocket.SocketServer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameServer implements Runnable {

    public final static GameServer INSTANCE = new GameServer();

    private final GameUniverse gameUniverse;
    private final GameEventDispatcher eventDispatcher;

    private final IServerConfiguration config;

    private SocketServer socketServer;

    private final int maxExecutionInstructions;

    private final DayNightCycle dayNightCycle;

    private final CryptoProvider cryptoProvider;

    private final MongoClient mongo;

    private final UserManager userManager;

    private final UserStatsHelper userStatsHelper;

    private final GameRegistry gameRegistry;

    private String secretKey;

    public final ReadWriteLock execLock;

    public GameServer() {
        this.config = new ServerConfiguration("config.properties");

        String connString = String.format("mongodb://%s:%d",
                config.getString("mongo_address"), config.getInt("mongo_port"));
        mongo = MongoClients.create(connString);
        MongoDatabase db = mongo.getDatabase(config.getString("mongo_dbname"));

        MongoCollection<Document> userCollection = db.getCollection("user");

        userManager = new UserManager(userCollection);
        userStatsHelper = new UserStatsHelper(userCollection);

        gameUniverse = new GameUniverse(config);
        gameUniverse.setMongo(mongo);
        gameRegistry = new GameRegistry();

        maxExecutionInstructions = config.getInt("user_instructions_per_tick");

        cryptoProvider = new CryptoProvider();

        dayNightCycle = new DayNightCycle();

        SecretKeyGenerator keyGenerator = new SecretKeyGenerator();
        secretKey = config.getString("secret_key");
        if (secretKey == null) {
            secretKey = keyGenerator.generate();
            config.setString("secret_key", secretKey);
        }

        eventDispatcher = new GameEventDispatcher();

        registerEventListeners();
        registerGameObjects();

        execLock = new ReentrantReadWriteLock();
    }

    private void registerEventListeners() {

        List<GameEventListener> listeners = eventDispatcher.getListeners();

        listeners.add(dayNightCycle);

        //Debug command Listeners
        listeners.add(new ComPortMsgCommandListener());
        listeners.add(new CreateWorldCommandListener());
        listeners.add(new KillAllCommandListener());
        listeners.add(new MoveObjCommandListener());
        listeners.add(new ObjInfoCommandListener());
        listeners.add(new SetTileAtCommandListener());
        listeners.add(new SpawnObjCommandListener());
        listeners.add(new TpObjectCommandListener());
        listeners.add(new UserInfoCommandListener());
        listeners.add(new HealObjCommandListener());
        listeners.add(new DamageObjCommandListener());
        listeners.add(new SetEnergyCommandListener());
        listeners.add(new SaveGameCommandListener());

        // Biomass
        listeners.add(new WorldCreationListener());
        listeners.add(new WorldUpdateListener(config));
        listeners.add(new ObjectDeathListener(config));

        // Cubot
        listeners.add(new CpuInitialisationListener());
        listeners.add(new UserCreationListener());

        listeners.add(new ChargeShieldCommandListener());
        listeners.add(new SetInventoryPosition());
        listeners.add(new PutItemCommandListener());
        listeners.add(new PopItemCommandListener());

        listeners.add(new DeathListener());
        listeners.add(new WalkListener());

        // NPC
        listeners.add(new net.simon987.mar.npc.event.WorldCreationListener(config.getInt("settlement_spawn_rate")));
        listeners.add(new net.simon987.mar.npc.event.CpuInitialisationListener());
        listeners.add(new VaultWorldUpdateListener(config));
        listeners.add(new VaultCompleteListener());
        listeners.add(new LoadListener());
        listeners.add(new BeforeSaveListener());
    }

    private void registerGameObjects() {
        gameRegistry.registerItem(ItemCopper.ID, ItemCopper.class);
        gameRegistry.registerItem(ItemIron.ID, ItemIron.class);

        gameRegistry.registerTile(TileVoid.ID, TileVoid.class);
        gameRegistry.registerTile(TilePlain.ID, TilePlain.class);
        gameRegistry.registerTile(TileWall.ID, TileWall.class);
        gameRegistry.registerTile(TileCopper.ID, TileCopper.class);
        gameRegistry.registerTile(TileIron.ID, TileIron.class);
        gameRegistry.registerTile(TileFluid.ID, TileFluid.class);

        // Biomass
        gameRegistry.registerGameObject(BiomassBlob.class);
        gameRegistry.registerItem(ItemBiomass.ID, ItemBiomass.class);

        // Construction
        gameRegistry.registerItem(ItemBluePrint.ID, ItemBluePrint.class);
        gameRegistry.registerGameObject(Obstacle.class);
        gameRegistry.registerGameObject(ConstructionSite.class);

        // Cubot
        gameRegistry.registerGameObject(Cubot.class);
        gameRegistry.registerHardware(CubotLeg.class);
        gameRegistry.registerHardware(CubotLaser.class);
        gameRegistry.registerHardware(CubotLidar.class);
        gameRegistry.registerHardware(CubotDrill.class);
        gameRegistry.registerHardware(CubotInventory.class);
        gameRegistry.registerHardware(CubotKeyboard.class);
        gameRegistry.registerHardware(CubotHologram.class);
        gameRegistry.registerHardware(CubotBattery.class);
        gameRegistry.registerHardware(CubotFloppyDrive.class);
        gameRegistry.registerHardware(CubotComPort.class);
        gameRegistry.registerHardware(CubotShield.class);
        gameRegistry.registerHardware(CubotCore.class);

        // Misc HW
        gameRegistry.registerHardware(RandomNumberGenerator.class);
        gameRegistry.registerHardware(Clock.class);

        // NPC
        gameRegistry.registerGameObject(HarvesterNPC.class);
        gameRegistry.registerGameObject(Factory.class);
        gameRegistry.registerGameObject(RadioTower.class);
        gameRegistry.registerGameObject(VaultDoor.class);
        gameRegistry.registerGameObject(net.simon987.mar.npc.Obstacle.class);
        gameRegistry.registerGameObject(ElectricBox.class);
        gameRegistry.registerGameObject(Portal.class);
        gameRegistry.registerGameObject(VaultExitPortal.class);
        gameRegistry.registerGameObject(HackedNPC.class);

        gameRegistry.registerHardware(RadioReceiverHardware.class);
        gameRegistry.registerHardware(NpcBattery.class);
        gameRegistry.registerHardware(NpcInventory.class);

        gameRegistry.registerTile(TileVaultFloor.ID, TileVaultFloor.class);
        gameRegistry.registerTile(TileVaultWall.ID, TileVaultWall.class);
    }

    public GameUniverse getUniverse() {
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

                    CPU cpu = user.getControlledUnit().getCpu();
                    int allocation = Math.min(user.getControlledUnit().getEnergy() * CPU.INSTRUCTION_COST, maxExecutionInstructions);
                    cpu.setInstructionAlloction(allocation);

                    if (!cpu.isPaused()) {
                        cpu.reset();
                        executeUserCode(user);
                    }

                } catch (Exception e) {
                    LogManager.LOGGER.severe("Error executing " + user.getUsername() + "'s code");
                    e.printStackTrace();
                }
            }
        }

        //Process each worlds
        GameServer.INSTANCE.execLock.writeLock().lock();
        for (World world : gameUniverse.getWorlds()) {
            if (world.shouldUpdate()) {
                world.update();
            }
        }

        //Save
        if (gameUniverse.getTime() % config.getInt("save_interval") == 0) {
            save();
        }
        GameServer.INSTANCE.execLock.writeLock().unlock();

        socketServer.tick();
    }

    public void executeUserCode(User user) {
        GameServer.INSTANCE.execLock.readLock().lock();
        int cost = user.getControlledUnit().getCpu().execute();
        user.getControlledUnit().spendEnergy(cost);
        user.addTime(cost);
        GameServer.INSTANCE.execLock.readLock().unlock();

        if (user.getControlledUnit().getCpu().isPaused()) {
            socketServer.promptUserPausedState(user);
        }
    }

    void load() {

        LogManager.LOGGER.info("Loading all data from MongoDB");

        MongoDatabase db = mongo.getDatabase(config.getString("mongo_dbname"));

        MongoCollection<Document> worlds = db.getCollection("world");
        MongoCollection<Document> server = db.getCollection("server");

        Document whereQuery = new Document();
        whereQuery.put("shouldUpdate", true);
        MongoCursor<Document> cursor = worlds.find(whereQuery).iterator();
        GameUniverse universe = GameServer.INSTANCE.getUniverse();
        while (cursor.hasNext()) {
            World w = World.deserialize(cursor.next());
            universe.addWorld(w);
        }

        //Load users
        ArrayList<User> userList = userManager.getUsers();
        for (User user : userList) {
            universe.addUser(user);
        }

        //Load server data
        cursor = server.find().iterator();
        if (cursor.hasNext()) {
            Document serverObj = cursor.next();
            gameUniverse.setTime((long) serverObj.get("time"));

            gameUniverse.store = (Map<String, Document>) serverObj.get("store");
        }

        eventDispatcher.dispatch(new LoadEvent());

        LogManager.LOGGER.info("Done loading! W:" + GameServer.INSTANCE.getUniverse().getWorldCount() +
                " | U:" + GameServer.INSTANCE.getUniverse().getUserCount());
    }

    public void save() {

        LogManager.LOGGER.info("Saving to MongoDB | W:" + gameUniverse.getWorldCount() + " | U:" + gameUniverse.getUserCount());

        eventDispatcher.dispatch(new BeforeSaveEvent());

        ClientSession session = null;
        try {
            try {
                session = mongo.startSession();
                session.startTransaction();
            } catch (MongoClientException e) {
                LogManager.LOGGER.fine("Could not create mongoDB session, will not use transaction feature. " +
                        "(This message can be safely ignored)");
            }

            MongoDatabase db = mongo.getDatabase(config.getString("mongo_dbname"));
            ReplaceOptions updateOptions = new ReplaceOptions();
            updateOptions.upsert(true);

            int unloaded_worlds = 0;

            MongoCollection<Document> worlds = db.getCollection("world");
            MongoCollection<Document> users = db.getCollection("user");
            MongoCollection<Document> server = db.getCollection("server");

            int insertedWorlds = 0;
            GameUniverse universe = GameServer.INSTANCE.getUniverse();
            for (World w : universe.getWorlds()) {
                insertedWorlds++;
                worlds.replaceOne(new Document("_id", w.getId()), w.mongoSerialise(), updateOptions);

                //If the world should unload, it is removed from the Universe after having been saved.
                if (w.shouldUnload()) {
                    unloaded_worlds++;
                    universe.removeWorld(w);
                }
            }

            for (User u : GameServer.INSTANCE.getUniverse().getUsers()) {
                if (!u.isGuest()) {
                    users.replaceOne(new Document("_id", u.getUsername()), u.mongoSerialise(), updateOptions);
                }
            }

            Document serverObj = new Document();
            serverObj.put("time", gameUniverse.getTime());
            serverObj.put("store", gameUniverse.store);

            //A constant id ensures only one entry is kept and updated, instead of a new entry created every save.
            server.replaceOne(new Document("_id", "serverinfo"), serverObj, updateOptions);
            if (session != null) {
                session.commitTransaction();
            }

            LogManager.LOGGER.info("" + insertedWorlds + " worlds saved, " + unloaded_worlds + " unloaded");
        } catch (Exception e) {
            LogManager.LOGGER.severe("Problem happened during save function");
            e.printStackTrace();

            if (session != null) {
                session.commitTransaction();
            }
        }
    }

    public IServerConfiguration getConfig() {
        return config;
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
