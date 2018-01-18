package net.simon987.npcplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.crypto.RandomStringGenerator;
import net.simon987.server.game.Enterable;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.Programmable;
import net.simon987.server.game.Updatable;
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


    /**
     * Number of ticks to remain the door open
     */
    private int OPEN_TIME = GameServer.INSTANCE.getConfig().getInt("vault_door_open_time");

    private int openedTimer = 0;
    private int cypherId;

    public VaultDoor(int cypherId) {
        this.cypherId = cypherId;
        this.randomStringGenerator = new RandomStringGenerator();

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

    private void openVault(){
        open = true;
        openedTimer = OPEN_TIME;
        LogManager.LOGGER.fine("Opened Vault door ID: " + getObjectId());
    }

    private void keepVaultOpen(){
        open = true;
        openedTimer = OPEN_TIME;
    }  

    @Override
    public boolean enter(GameObject object) {

        LogManager.LOGGER.fine("VAULT enter " + open);

        if (open) {
            //TODO: Enter in the vault
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
        dbObject.put("t", ID);

        return dbObject;
    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();

        json.put("i", getObjectId());
        json.put("x", getX());
        json.put("y", getY());
        json.put("t", ID);

        return json;
    }

    public static VaultDoor deserialize(DBObject obj) {

        VaultDoor vaultDoor = new VaultDoor(0); //cypherId ?
        vaultDoor.setObjectId((long) obj.get("i"));
        vaultDoor.setX((int) obj.get("x"));
        vaultDoor.setY((int) obj.get("y"));

        return vaultDoor;
    }

}
