package net.simon987.cubotplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.Memory;
import net.simon987.server.game.*;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.User;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Cubot extends GameObject implements Updatable, ControllableUnit, Programmable, Attackable, Rechargeable {

    private static final char MAP_INFO = 0x0080;
    public static final int ID = 1;

    private int hologram = 0;
    private String hologramString = "";
    private HologramMode hologramMode = HologramMode.CLEARED;
    private HologramMode lastHologramMode = HologramMode.CLEARED;
    private int hologramColor = 0;

    /**
     * Hit points
     */
    private int hp;
    private int maxHp;
    private int shield;
    private int maxShield;
    private int heldItem;

    private Action currentAction = Action.IDLE;
    private Action lastAction = Action.IDLE;

    private char currentStatus;
    private char lastStatus;

    private ArrayList<Integer> keyboardBuffer = new ArrayList<>();

    private ArrayList<char[]> consoleMessagesBuffer = new ArrayList<>(CONSOLE_BUFFER_MAX_SIZE);
    private ArrayList<char[]> lastConsoleMessagesBuffer = new ArrayList<>(CONSOLE_BUFFER_MAX_SIZE);
    private ConsoleMode consoleMode = ConsoleMode.NORMAL;
    private ConsoleMode lastConsoleMode = ConsoleMode.NORMAL;

    private User parent;

    private int energy;
    private int maxEnergy;

    private static final float SOLAR_PANEL_MULTIPLIER = 1;
    private static final int CONSOLE_BUFFER_MAX_SIZE = 40;

    public Cubot() {

    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public void update() {

        storeEnergy((int) (SOLAR_PANEL_MULTIPLIER * GameServer.INSTANCE.getDayNightCycle().getSunIntensity()));

        if (currentAction == Action.WALKING) {
            if (spendEnergy(100)) {
                if (!incrementLocation()) {
                    //Couldn't walk
                    currentAction = Action.IDLE;
                }
            } else {
                currentAction = Action.IDLE;
            }
        }

        /*
         * CurrentAction is set during the code execution and this function is called right after
         * If no action as been set, the action sent to the client is the action in currentAction that
         * was set last tick (IDLE)
         */
        lastAction = currentAction;
        currentAction = Action.IDLE;

        //Same principle for hologram
        lastHologramMode = hologramMode;
        hologramMode = HologramMode.CLEARED;

        //And the console
        lastConsoleMode = consoleMode;
        consoleMode = ConsoleMode.NORMAL;

        lastConsoleMessagesBuffer = new ArrayList<>(consoleMessagesBuffer);
        consoleMessagesBuffer.clear();

        //And the status..
        lastStatus = currentStatus;
        currentStatus = 0;
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();
        json.put("i", getObjectId());
        json.put("t", ID);
        json.put("x", getX());
        json.put("y", getY());
        json.put("direction", getDirection().ordinal());
        json.put("heldItem", heldItem);
        json.put("hp", hp);
        json.put("shield", shield);
        json.put("action", lastAction.ordinal());
        json.put("holo", hologram);
        json.put("holoStr", hologramString);
        json.put("holoMode", lastHologramMode.ordinal());
        json.put("holoC", hologramColor);
        json.put("energy", energy);

        if (parent != null) {
            json.put("parent", parent.getUsername()); //Only used client-side for now
        }

        return json;
    }

    @Override
    public BasicDBObject mongoSerialise() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("i", getObjectId());
        dbObject.put("t", ID);
        dbObject.put("x", getX());
        dbObject.put("y", getY());
        dbObject.put("direction", getDirection().ordinal());
        dbObject.put("heldItem", heldItem);
        dbObject.put("hp", hp);
        dbObject.put("shield", shield);
        dbObject.put("action", lastAction.ordinal());
        dbObject.put("holo", hologram);
        dbObject.put("holoStr", hologramString);
        dbObject.put("holoMode", lastHologramMode.ordinal());
        dbObject.put("holoC", hologramColor);
        dbObject.put("energy", energy);

        if (parent != null) {
            dbObject.put("parent", parent.getUsername()); //Only used client-side for now
        }

        return dbObject;
    }

    public static Cubot deserialize(DBObject obj) {

        Cubot cubot = new Cubot();
        cubot.setObjectId((long) obj.get("i"));
        cubot.setX((int) obj.get("x"));
        cubot.setY((int) obj.get("y"));
        cubot.hp = (int) obj.get("hp");
        cubot.shield = (int) obj.get("shield");
        cubot.setDirection(Direction.getDirection((int) obj.get("direction")));
        cubot.heldItem = (int) obj.get("heldItem");
        cubot.energy = (int) obj.get("energy");

        ServerConfiguration config = GameServer.INSTANCE.getConfig();
        cubot.maxEnergy = config.getInt("battery_max_energy");
        cubot.maxHp = config.getInt("cubot_max_hp");
        cubot.maxShield = config.getInt("cubot_max_shield");

        return cubot;

    }

    public void setHeldItem(int heldItem) {
        this.heldItem = heldItem;
    }

    public int getHeldItem() {
        return heldItem;
    }

    @Override
    public void setKeyboardBuffer(ArrayList<Integer> kbBuffer) {
        keyboardBuffer = kbBuffer;
    }

    @Override
    public ArrayList<Integer> getKeyboardBuffer() {
        return keyboardBuffer;
    }

    public void clearKeyboardBuffer() {
        keyboardBuffer.clear();
    }

    public void setCurrentAction(Action currentAction) {
        this.currentAction = currentAction;
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public Action getAction() {
        return lastAction;
    }

    public Action getCurrentAction() {
        return currentAction;
    }

    public void setHologram(int hologram) {
        this.hologram = hologram;
    }


    public void setHologramString(String hologramString) {
        this.hologramString = hologramString;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public boolean spendEnergy(int amount) {

        if (energy - amount < 0) {
            return false;
        } else {
            energy -= amount;
            return true;
        }
    }

    public void storeEnergy(int amount) {

        energy = Math.min(energy + amount, maxEnergy);
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getShield() {
        return shield;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }

    public boolean chargeShield(int amount) {
        amount = Math.min(amount, maxShield - shield);

        int energySpent = amount * CubotShield.COST;
        if(spendEnergy(energySpent)) {
            shield += amount;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Damages shield by amount.
     * 
     * Return damage that broke through the shield.
     */
    public int damageShield(int amount) {
        int after = shield - amount;
        if(after < 0) {
            shield = 0;
            return -after;
        }
        shield = after;
        return 0;
    }

    @Override
    public Memory getFloppyData() {

        CubotFloppyDrive drive = ((CubotFloppyDrive) getParent().getCpu().getHardware(CubotFloppyDrive.DEFAULT_ADDRESS));

        if (drive.getFloppy() != null) {
            return drive.getFloppy().getMemory();
        } else {
            return null;
        }
    }

    @Override
    public boolean isAt(int x, int y) {
        return false;
    }

    public void setHologramMode(HologramMode hologramMode) {
        this.hologramMode = hologramMode;
    }

    public enum HologramMode {
        CLEARED,
        HEX,
        STRING,
        DEC
    }

    public enum ConsoleMode {
        CLEAR,
        NORMAL
    }

    @Override
    public void setAction(Action action) {
        currentAction = action;
    }

    @Override
    public boolean sendMessage(char[] message) {

        if (consoleMessagesBuffer.size() < CONSOLE_BUFFER_MAX_SIZE) {
            consoleMessagesBuffer.add(message);
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<char[]> getConsoleMessagesBuffer() {
        return lastConsoleMessagesBuffer;
    }


    public int getConsoleMode() {
        return lastConsoleMode.ordinal();
    }

    public void setConsoleMode(ConsoleMode consoleMode) {
        this.consoleMode = consoleMode;
    }

    public void setHologramColor(int hologramColor) {
        this.hologramColor = hologramColor;
    }

    public void addStatus(CubotStatus status) {

        currentStatus |= status.val;
    }

    public void removeStatus(CubotStatus status) {

        currentStatus &= (~status.val);
    }

    public char getStatus() {
        return lastStatus;
    }

    @Override
    public void setHealRate(int hp) {
        //no op
    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public void setHp(int hp) {
        this.hp = hp;
    }

    @Override
    public int getMaxHp() {
        return maxHp;
    }

    @Override
    public void setMaxHp(int hp) {
        this.maxHp = hp;
    }

    @Override
    public void heal(int amount) {
        hp += amount;

        //Can't heal above max
        if (hp > maxHp) {
            hp = maxHp;
        }
    }

    @Override
    public void damage(int amount) {

        //Damage shield first
        int hullDamage = damageShield(amount);

        hp -= hullDamage;

        if (hp <= 0) {
            setDead(true);
        }
    }

    public void reset() {
        setDead(false);
        setHp(maxHp);
        setShield(0);
        setHeldItem(0);
        setEnergy(maxEnergy);
        clearKeyboardBuffer();
        consoleMessagesBuffer.clear();
        lastConsoleMessagesBuffer.clear();
        hologramColor = 0;
        currentStatus = 0;
        lastStatus = 0;
        addStatus(CubotStatus.FACTORY_NEW);
    }

    @Override
    public boolean onDeadCallback() {
        LogManager.LOGGER.info(getParent().getUsername() + "'s Cubot died");

        reset();

        //Teleport to spawn point
        this.getWorld().removeObject(this);
        this.getWorld().decUpdatable();

        ServerConfiguration config = GameServer.INSTANCE.getConfig();
        Random random = new Random();

        int spawnX = config.getInt("new_user_worldX") + random.nextInt(5);
        int spawnY = config.getInt("new_user_worldY") + random.nextInt(5);
        String dimension = config.getString("new_user_dimension");
        this.setWorld(GameServer.INSTANCE.getGameUniverse().getWorld(spawnX, spawnY, true, dimension));

        Point point = this.getWorld().getRandomPassableTile();
        this.setX(point.x);
        this.setY(point.y);

        this.getWorld().addObject(this);
        this.getWorld().incUpdatable();

        return true;
    }

    public int getMaxShield() {
        return maxShield;
    }

    public void setMaxShield(int maxShield) {
        this.maxShield = maxShield;
    }
}
