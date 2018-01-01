package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.Memory;
import net.simon987.server.game.*;
import net.simon987.server.user.User;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Cubot extends GameObject implements Updatable, ControllableUnit, Programmable, Attackable {

    private static final char MAP_INFO = 0x0080;
    public static final int ID = 1;

    public static int TYPE_ID = 2;
    public static final int MAX_HEALTH = 100;
    public static final int HEAL_RATE = 5;

    private int hologram = 0;
    private String hologramString = "";
    private HologramMode hologramMode = HologramMode.CLEARED;
    private HologramMode lastHologramMode = HologramMode.CLEARED;
    private int hologramColor = 0;

    /**
     * Hit points
     */
    private int hp;
    private int heldItem;
    private int healRate;
    private int maxHp;

    private Action currentAction = Action.IDLE;
    private Action lastAction = Action.IDLE;

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

    public static Cubot deserialize(JSONObject json) {

        Cubot cubot = new Cubot();
        cubot.setObjectId((long) json.get("i"));
        cubot.setX((int) (long) json.get("x"));
        cubot.setY((int) (long) json.get("y"));
        cubot.hp = (int) (long) json.get("hp");
        cubot.setDirection(Direction.getDirection((int) (long) json.get("direction")));
        cubot.heldItem = (int) (long) json.get("heldItem");
        cubot.energy = (int) (long) json.get("energy");
        cubot.maxEnergy = GameServer.INSTANCE.getConfig().getInt("battery_max_energy");

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

    public boolean spendEnergy(int spent) {

        if (energy - spent < 0) {
            return false;
        } else {
            energy -= spent;
            return true;
        }
    }

    public void storeEnergy(int qty) {

        energy = Math.min(energy + qty, maxEnergy);

    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
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

	@Override
	public void setHealRate(int hp) {
		healRate = hp;
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
		maxHp = hp;
	}

	@Override
	public void heal(int amount) {
        hp += amount;
        if(hp > maxHp) {
            hp = maxHp;
        }
	}

	@Override
	public void damage(int amount) {
        hp -= amount;
        if(hp <= 0) {
            hp = 0;
            // TODO: handle death here somehow.
        }
	}
}
