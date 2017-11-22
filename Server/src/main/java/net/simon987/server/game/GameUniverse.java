package net.simon987.server.game;

import net.simon987.server.GameServer;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.Assembler;
import net.simon987.server.assembly.AssemblyResult;
import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.exception.CancelledException;
import net.simon987.server.io.JSONSerialisable;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GameUniverse implements JSONSerialisable {

    private ArrayList<World> worlds;
    private ArrayList<User> users;
    private WorldGenerator worldGenerator;

    private long time;

    private int nextObjectId = 0;

    private int maxWidth = 0xFFFF;

    public GameUniverse(ServerConfiguration config) {

        worlds = new ArrayList<>(32);
        users = new ArrayList<>(16);

        worldGenerator = new WorldGenerator(config);

    }

    public long getTime() {
        return time;
    }

    public World getWorld(int x, int y) {

        for (World world : worlds) {
            if (world.getX() == x && world.getY() == y) {
                return world;
            }
        }

        if (x >= 0 && x <= maxWidth && y >= 0 && y <= maxWidth) {
            //World does not exist
            LogManager.LOGGER.severe("Trying to read a World that does not exist!");

            World world = createWorld(x, y);

            worlds.add(world);

            return world;
        } else {
            return null;
        }
    }

    public World createWorld(int x, int y) {

        World world = null;
        try {
            world = worldGenerator.generateWorld(x, y);


        } catch (CancelledException e) {
            e.printStackTrace();
        }

        return world;
    }

    public User getUser(String username) {

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
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
                    user.getCpu().setCodeSegmentOffset(ar.getCodeSegmentOffset());


                } else {
                    user = new User(null);
                }

                user.setUsername(username);

                users.add(user);

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
    public GameObject getObject(int id) {

        //
        for (World world : worlds) {
            for (GameObject object : world.getGameObjects()) {
                if (object.getObjectId() == id) {
                    return object;
                }
            }
        }

        return null;
    }


    public void incrementTime() {
        time++;
    }

    public ArrayList<World> getWorlds() {
        return worlds;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();

        JSONArray worlds = new JSONArray();

        ArrayList<World> worlds_ = new ArrayList<>(this.worlds);
        for (World world : worlds_) {
            worlds.add(world.serialise());
        }

        JSONArray users = new JSONArray();
        ArrayList<User> users_ = new ArrayList<User>(this.users);
        for (User user : users_) {
            if (!user.isGuest()) {
                users.add(user.serialise());
            }

        }


        json.put("users", users);
        json.put("worlds", worlds);
        json.put("time", time);
        json.put("nextObjectId", nextObjectId);

        return json;
    }

    /**
     * Load game universe from JSON save file
     *
     * @param file JSON save file
     */
    public void load(File file) {

        JSONParser parser = new JSONParser();

        if (file.isFile()) {
            try {

                FileReader reader = new FileReader(file);
                JSONObject universeJson = (JSONObject) parser.parse(reader);

                time = (long) universeJson.get("time");
                nextObjectId = (int) (long) universeJson.get("nextObjectId");

                for (JSONObject worldJson : (ArrayList<JSONObject>) universeJson.get("worlds")) {
                    worlds.add(World.deserialize(worldJson));
                }

                for (JSONObject userJson : (ArrayList<JSONObject>) universeJson.get("users")) {
                    users.add(User.deserialize(userJson));
                }

                LogManager.LOGGER.info("Loaded " + worlds.size() + " worlds from file");

                reader.close();

            } catch (IOException | ParseException | CancelledException e) {
                e.printStackTrace();
            }
        } else {
            LogManager.LOGGER.severe("Couldn't load save file save.json, creating empty game universe.");
        }


    }

    public int getNextObjectId() {
        return ++nextObjectId;
    }

    public String getGuestUsername() {
        int i = 1;

        while (i < 1000) { //todo get Max guest user cap from config
            if (getUser("guest" + String.valueOf(i)) != null) {
                i++;
                continue;
            }

            return "guest" + String.valueOf(i);
        }

        return null;

    }

    public void removeUser(User user) {
        users.remove(user);

    }

    public int getMaxWidth() {
        return maxWidth;
    }
}
