package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.Memory;
import net.simon987.server.game.objects.*;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.User;
import org.bson.Document;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Cubot extends GameObject implements Updatable, ControllableUnit, Programmable, Attackable, Rechargeable {

    private static final char MAP_INFO = 0x0080;

    /**
     * Hologram value that is displayed
     * <br>TODO: Move to CubotHologram class
     */
    private int hologram = 0;
    /**
     * Hologram string that is displayed
     * <br>TODO: Move to CubotHologram class
     */
    private String hologramString = "";
    /**
     * Hologram mode that was set during this tick
     * <br>TODO: Move to CubotHologram class
     */
    private HologramMode hologramMode = HologramMode.CLEARED;
    /**
     * Hologram mode at the end of the last tick
     * <br>TODO: Move to CubotHologram class
     */
    private HologramMode lastHologramMode = HologramMode.CLEARED;
    /**
     * Hologram color code. Format is handled by the client
     * <br>TODO: Move to CubotHologram class
     */
    private int hologramColor = 0;

    /**
     * Hit points
     */
    private int hp;
    /**
     * Maximum hit points
     */
    private int maxHp;

    /**
     * Shield points
     */
    private int shield;

    /**
     * Maximum shield points
     */
    private int maxShield;

    /**
     * Item ID of the current 'active' item
     */
    private int heldItem;

    /**
     * Action that was set during the current tick. It is set to IDLE by default
     */
    private Action currentAction = Action.IDLE;

    /**
     * Action at the end of the last tick
     */
    private Action lastAction = Action.IDLE;

    /**
     * Status bit field that was set during the current tick. It is set to 0 by default
     * <br>See CubotStatus and addStatus() method
     */
    private char currentStatus;

    /**
     * Status bit field at the end of the last tick
     */
    private char lastStatus;

    /**
     * Buffer of keypress codes. It is not changed between ticks and it is reset when
     * the player uploads their code
     */
    private ArrayList<Integer> keyboardBuffer = new ArrayList<>();

    /**
     * Buffer of console messages (also called 'internal buffer') that was set during the current tick
     */
    private ArrayList<char[]> consoleMessagesBuffer = new ArrayList<>(CONSOLE_BUFFER_MAX_SIZE);
    /**
     * Buffer of console messages (also called 'internal buffer') at the end of the last tick
     */
    private ArrayList<char[]> lastConsoleMessagesBuffer = new ArrayList<>(CONSOLE_BUFFER_MAX_SIZE);
    /**
     * Console mode that was set during the current tick. It is set to NORMAL by default
     */
    private ConsoleMode consoleMode = ConsoleMode.NORMAL;
    /**
     * Console mode at the end of the last tick
     */
    private ConsoleMode lastConsoleMode = ConsoleMode.NORMAL;

    /**
     * User that controls this Cubot
     */
    private User parent;

    /**
     * Energy units in kJ
     */
    private int energy;

    /**
     * Maximum energy units in kJ
     */
    private int maxEnergy;

    /**
     * Solar panel multiplier
     * <br>TODO: Set this constant in dimension
     */
    private static final float SOLAR_PANEL_MULTIPLIER = 1;
    /**
     * Maximum size of the console buffer (also called 'internal buffer')
     */
    private static final int CONSOLE_BUFFER_MAX_SIZE = 40;

    /**
     * Display mode of the hologram hardware
     * <br>TODO: move this inside CubotHologram class
     */
    public enum HologramMode {
        /**
         * Display nothing
         */
        CLEARED,
        /**
         * Display value as hexadecimal in format 0x0000
         */
        HEX,
        /**
         * Display string
         */
        STRING,
        /**
         * Display value as decimal
         */
        DEC
    }

    public enum ConsoleMode {
        /**
         * Used by the ComPort hardware - clears the console screen (client-side)
         */
        CLEAR,
        /**
         * No specific client-side action
         */
        NORMAL
    }

    public Cubot() {

    }

    public Cubot(Document document) {
        super(document);

        hp = document.getInteger("hp");
        shield = document.getInteger("shield");
        setDirection(Direction.getDirection(document.getInteger("direction")));
        heldItem = document.getInteger("heldItem");
        energy = document.getInteger("energy");

        ServerConfiguration config = GameServer.INSTANCE.getConfig();
        maxEnergy = config.getInt("battery_max_energy");
        maxHp = config.getInt("cubot_max_hp");
        maxShield = config.getInt("cubot_max_shield");
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    /**
     * Called every tick
     */
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
    public JSONObject jsonSerialise() {
        JSONObject json = super.jsonSerialise();
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
    public Document mongoSerialise() {
        Document dbObject = super.mongoSerialise();

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

    /**
     * Reset to 'factory settings', as it were when it was first created
     */
    private void reset() {
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

    /**
     * Currently has no effect
     */
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

    public int getMaxShield() {
        return maxShield;
    }

    public void setMaxShield(int maxShield) {
        this.maxShield = maxShield;
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
}
