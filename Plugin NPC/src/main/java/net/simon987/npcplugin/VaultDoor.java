package net.simon987.npcplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.crypto.RandomStringGenerator;
import net.simon987.server.game.*;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

import java.util.Arrays;


public class VaultDoor extends GameObject implements Programmable, Enterable, Updatable {

    private static final int MAP_INFO = 0x0800;

    public static final int ID = 5;
    /**
     * Password to open the vault door
     */
    private char[] password;

    private RandomStringGenerator randomStringGenerator;

    /**
     * Whether or not the vault door is opened
     */
    private boolean open = false;

    private int homeX;
    private int homeY;
    private World homeWorld;


    /**
     * Number of ticks to remain the door open
     */
    private int OPEN_TIME = GameServer.INSTANCE.getConfig().getInt("vault_door_open_time");

    private int openedTimer = 0;
    private int cypherId;

    public VaultDoor(int cypherId) {
        this.cypherId = cypherId;

        this.randomStringGenerator = new RandomStringGenerator();

        this.password = "12345678".toCharArray();
    }


    @Override
    public void update() {
        if (open){
            if (openedTimer <= 0) {
                //Door was open for OPEN_TIME, close it and regen password
                //password = GameServer.INSTANCE.getConfig().getRandomPassword();
                open = false;
                openedTimer = 0;
                LogManager.LOGGER.fine("Closed Vault door ID: " + getObjectId());
            } else {
                openedTimer--;
            }
        }

    }

    @Override
    public boolean sendMessage(char[] message) {

        System.out.println("message: " + new String(message));
        System.out.println("password: " + new String(password));

        if (Arrays.equals(message, password)) {
            if (!open) {
                openVault();
            } else {
                keepVaultOpen();
            }
            return true;
        } else {
            return false;
        }
    }

    private void openVault() {
        open = true;
        openedTimer = OPEN_TIME;
        LogManager.LOGGER.fine("Opened Vault door ID: " + getObjectId());
    }

    private void keepVaultOpen() {
        open = true;
        openedTimer = OPEN_TIME;
    }

    @Override
    public boolean enter(GameObject object) {

        LogManager.LOGGER.fine("VAULT enter " + open);

        if (open) {

            object.getWorld().decUpdatable();
            object.getWorld().removeObject(object);

            homeWorld.incUpdatable();
            homeWorld.addObject(object);
            object.setWorld(homeWorld);
            object.setX(homeX);
            object.setY(homeY);

            return true;
        } else {
            return false;
        }
    }


    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public BasicDBObject mongoSerialise() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("i", getObjectId());
        dbObject.put("x", getX());
        dbObject.put("y", getY());
        dbObject.put("homeX", getHomeX());
        dbObject.put("homeY", getHomeY());
        dbObject.put("t", ID);
        dbObject.put("pw", new String(password));

        return dbObject;
    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();

        json.put("i", getObjectId());
        json.put("x", getX());
        json.put("y", getY());
        json.put("t", ID);
        //Don't send the password to the client!

        return json;
    }

    public static VaultDoor deserialize(DBObject obj) {

        VaultDoor vaultDoor = new VaultDoor(0); //cypherId ?
        vaultDoor.setX((int) obj.get("x"));
        vaultDoor.setY((int) obj.get("y"));
        vaultDoor.setObjectId((long) obj.get("i"));


        if (obj.containsField("homeX") && obj.containsField("homeY")) {
            vaultDoor.setHomeX((int) obj.get("homeX"));
            vaultDoor.setHomeY((int) obj.get("homeY"));
        }

        vaultDoor.password = ((String) obj.get("pw")).toCharArray();

        return vaultDoor;
    }

    @Override
    public void initialize() {
        //Get or generate vault world
        homeWorld = GameServer.INSTANCE.getGameUniverse().getWorld(0x7FFF, 0x7FFF,
                false, "v" + getObjectId() + "-");

        if (homeWorld == null) {
            VaultDimension vaultDimension = new VaultDimension(this);
            homeWorld = vaultDimension.getHomeWorld();
            homeX = vaultDimension.getHomeX();
            homeY = vaultDimension.getHomeY();
        }
    }

    public int getHomeX() {
        return homeX;
    }

    public void setHomeX(int homeX) {
        this.homeX = homeX;
    }

    public int getHomeY() {
        return homeY;
    }

    public void setHomeY(int homeY) {
        this.homeY = homeY;
    }
}
