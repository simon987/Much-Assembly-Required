package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.Memory;
import net.simon987.server.game.*;
import net.simon987.server.user.User;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Cubot extends GameObject implements Updatable, ControllableUnit {

    private static final char MAP_INFO = 0x0080;
    public static final int ID = 1;

    private char hologram = 0;
    private char lastHologram = 0;

    /**
     * Hit points
     */
    private int hp;
    private int heldItem;

    private Action currentAction = Action.IDLE;
    private Action lastAction = Action.IDLE;

    private ArrayList<Integer> keyboardBuffer = new ArrayList<>();

    private FloppyDisk floppyDisk;

    private User parent;

    private int energy;
    private int maxEnergy;

    private static final float SOLAR_PANEL_MULTIPLIER = 1;

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
        lastHologram = hologram;
        hologram = 0;
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
        json.put("holo", (int) lastHologram);
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

    public void setHologram(char hologram) {
        this.hologram = hologram;
    }

    public char getHologram() {
        return lastHologram;
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
}
