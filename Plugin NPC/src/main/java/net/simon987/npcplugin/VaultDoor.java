package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.crypto.RandomStringGenerator;
import net.simon987.server.game.objects.*;
import net.simon987.server.game.world.World;
import net.simon987.server.logging.LogManager;
import org.bson.Document;

import java.util.Arrays;


public class VaultDoor extends Structure implements MessageReceiver, Enterable, Updatable {

    private static final int MAP_INFO = 0x0B00;

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
        super(1, 1);

        this.cypherId = cypherId;

        this.randomStringGenerator = new RandomStringGenerator();

        this.password = "12345678".toCharArray();
    }

    public VaultDoor(Document document) {
        super(document, 1, 1);

        setX(document.getInteger("x"));
        setY(document.getInteger("y"));


        if (document.containsKey("homeX") && document.containsKey("homeY")) {
            homeX = document.getInteger("homeX");
            homeY = document.getInteger("homeY");
        }

        password = document.getString("password").toCharArray();
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

//        LogManager.LOGGER.fine("VAULT enter " + open);

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
    public Document mongoSerialise() {
        Document dbObject = super.mongoSerialise();

        dbObject.put("homeX", getHomeX());
        dbObject.put("homeY", getHomeY());
        dbObject.put("password", new String(password));

        return dbObject;
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
