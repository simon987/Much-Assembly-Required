package net.simon987.server.game;

import com.mongodb.*;
import net.simon987.server.GameServer;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.Assembler;
import net.simon987.server.assembly.AssemblyResult;
import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.exception.CancelledException;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.User;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class GameUniverse {

    //private ArrayList<World> worlds;
    private ConcurrentHashMap<String, World> worlds;
    //username:user
    private ConcurrentHashMap<String, User> users;
    private WorldGenerator worldGenerator;

    private MongoClient mongo = null;


    private long time;

    private long nextObjectId = 0;

    private int maxWidth = 0xFFFF;

    public GameUniverse(ServerConfiguration config) {

        worlds = new ConcurrentHashMap<>(256);
        users = new ConcurrentHashMap<>(16);

        worldGenerator = new WorldGenerator(config);
    }

    public void setMongo(MongoClient mongo){
        this.mongo = mongo;
    }

    public long getTime() {
        return time;
    }

    /**
     * Attempts loading a world from mongoDB by coordinates
     *
     * @param x     the x coordinate of the world
     * @param y     the y coordinate of the world
     *
     * @return World, null if not found
     */
    private World loadWorld(int x, int y){
        
        DB db = mongo.getDB("mar");
        DBCollection worlds = db.getCollection("world");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("_id", World.idFromCoordinates(x,y));
        DBCursor cursor = worlds.find(whereQuery);
        if (cursor.hasNext()) {
            World w = World.deserialize(cursor.next());
            return w;
        }
        else{
            return null;
        }
    }

    /**
     * Get a world by coordinates, attempts to load from mongoDB if not found.
     * 
     * @param x             the x coordinate of the world
     * @param y             the y coordinate of the world
     * @param createNew     if true, a new world is created when a world with given coordinates is not found
     *
     * @return World, null if not found and not created. 
     */
    public World getWorld(int x, int y, boolean createNew) {

        // Wrapping coordinates around cyclically
        x %= maxWidth+1;
        y %= maxWidth+1;

        // Looks for a previously loaded world
        World world = getLoadedWorld(x,y);
        if (world != null){
            return world;
        }

        // Tries loading the world from the database
        world = loadWorld(x,y);
        if (world != null){
            addWorld(world);
            LogManager.LOGGER.fine("Loaded world "+(World.idFromCoordinates(x,y))+" from mongodb.");
            return world;
        }

        // World does not exist
        if (createNew) {
            // Creates a new world
            world = createWorld(x, y);
            addWorld(world);
            LogManager.LOGGER.fine("Created new world "+(World.idFromCoordinates(x,y))+".");
            return world;
        } else {
            return null;
        }
    }    

    public World getLoadedWorld(int x, int y) {
        // Wrapping coordinates around cyclically
        x %= maxWidth+1;
        y %= maxWidth+1;

        return worlds.get(World.idFromCoordinates(x,y));
    }    

    /**
     * Adds a new or freshly loaded world to the universe (if not already present).
     * 
     * @param world     the world to be added
     */
    public void addWorld(World world){
        World w = worlds.get(world.getId());
        if (w == null){
            world.setUniverse(this);
            worlds.put(world.getId(),world);
        }
    }

    /**
     * Removes the world with given coordinates from the universe.
     * 
     * @param x     the x coordinate of the world to be removed
     * @param y     the y coordinate of the world to be removed
     */
    public void removeWorld(int x, int y){
        World w = worlds.remove(World.idFromCoordinates(x,y));
        if (w != null){
            w.setUniverse(null);
        }
    }

    /**
     * Removes the given world from the universe.
     * 
     * @param world     the world to be removed.
     */
    public void removeWorld(World world){
        World w = worlds.remove(world.getId());
        if (w != null){
            w.setUniverse(null);
        }
    }

    private World createWorld(int x, int y) {
        World world = null;
        try {
            world = worldGenerator.generateWorld(x, y);
        } catch (CancelledException e) {
            e.printStackTrace();
        }
        return world;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public User getOrCreateUser(String username, boolean makeControlledUnit) {
        User user = getUser(username);

        if (user != null) {
            return user;
        } else {

            LogManager.LOGGER.info("Creating new User: " + username);

            try {
                if (makeControlledUnit) {
                    user = new User();
                    user.setCpu(new CPU(GameServer.INSTANCE.getConfig(), user));
                    user.setUserCode(GameServer.INSTANCE.getConfig().getString("new_user_code"));

                    //Compile user code
                    AssemblyResult ar = new Assembler(user.getCpu().getInstructionSet(), user.getCpu().getRegisterSet(),
                            GameServer.INSTANCE.getConfig()).parse(user.getUserCode());

                    user.getCpu().getMemory().clear();

                    //Write assembled code to mem
                    char[] assembledCode = ar.getWords();

                    user.getCpu().getMemory().write((char) ar.origin, assembledCode, 0, assembledCode.length);
                    user.getCpu().setCodeSectionOffset(ar.getCodeSectionOffset());


                } else {
                    user = new User(null);
                }

                user.setUsername(username);

                addUser(user);

                return user;

            } catch (CancelledException e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    /**
     * Get an object by id
     * <br>
     * ConcurrentModificationException risk when inside game loop
     *
     * @param id id of the game object
     * @return GameObject, null if not found
     */
    public GameObject getObject(long id) {

        for (World world : getWorlds()) {
            GameObject obj = world.findObject(id);

            if (obj != null) {
                return obj;
            }
        }

        LogManager.LOGGER.severe("Couldn't find object: " + id);
        return null;
    }


    public void incrementTime() {
        time++;
    }

    public Collection<World> getWorlds() {
        return worlds.values();
    }

    public int getWorldCount() {
        return worlds.size();
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public int getUserCount() {
        return users.size();
    }

    public long getNextObjectId() {
        return ++nextObjectId;
    }

    public String getGuestUsername() {
        int i = 1;

        while (i < 10000) { //todo get Max guest user cap from config
            if (getUser("guest" + String.valueOf(i)) != null) {
                i++;
                continue;
            }

            return "guest" + String.valueOf(i);
        }

        return null;

    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public void removeUser(User user) {
        users.remove(user.getUsername());
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setNextObjectId(long nextObjectId) {
        this.nextObjectId = nextObjectId;
    }
}
