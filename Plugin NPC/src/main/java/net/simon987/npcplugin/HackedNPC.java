package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.*;
import net.simon987.server.event.ObjectDeathEvent;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.item.ItemVoid;
import net.simon987.server.game.objects.Action;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.Direction;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.User;
import org.bson.Document;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HackedNPC extends NonPlayerCharacter implements ControllableUnit {

    private static final int MEM_SIZE = GameServer.INSTANCE.getConfig().getInt("hacked_npc_mem_size");
    private static final boolean DIE_ON_NO_ENERGY = GameServer.INSTANCE.getConfig().getInt("hacked_npc_die_on_no_energy") != 0;

    private CPU cpu;
    /**
     * List of attached hardware, 'modules'
     */
    private Map<Integer, HardwareModule> hardwareAddresses = new HashMap<>();
    private Map<Class<? extends HardwareModule>, Integer> hardwareModules = new HashMap<>();

    private Action currentAction = Action.IDLE;
    private Action lastAction = Action.IDLE;
    private ArrayList<char[]> consoleMessagesBuffer = new ArrayList<>(30); //todo load from conf
    private ArrayList<char[]> lastConsoleMessagesBuffer = new ArrayList<>(30);

    HackedNPC(char[] program) {

        cpu = new CPU();

        cpu.setMemory(new Memory(MEM_SIZE));
        cpu.setHardwareHost(this);
        cpu.getMemory().write(cpu.getCodeSectionOffset(), program, 0, program.length);

        for (Object serialisedHw : (List) NpcPlugin.DEFAULT_HACKED_NPC.get("hardware")) {
            HardwareModule hardware = GameServer.INSTANCE.getRegistry().deserializeHardware((Document) serialisedHw, this);
            hardware.setCpu(cpu);
            attachHardware(hardware, ((Document) serialisedHw).getInteger("address"));
        }

        setTask(new ExecuteCpuTask());
    }

    public HackedNPC(Document document) {
        super(document);

        setHp(document.getInteger("hp"));
        setDirection(Direction.getDirection(document.getInteger("direction")));

        cpu = new CPU();
        cpu.setHardwareHost(this);
        cpu.setMemory(new Memory((Document) document.get("memory")));
        cpu.setRegisterSet(RegisterSet.deserialize((Document) document.get("registerSet")));

        ArrayList hardwareList = (ArrayList) document.get("hardware");

        for (Object serialisedHw : hardwareList) {
            HardwareModule hardware = GameServer.INSTANCE.getRegistry().deserializeHardware((Document) serialisedHw, this);
            hardware.setCpu(cpu);
            attachHardware(hardware, ((Document) serialisedHw).getInteger("address"));
        }

        setTask(new ExecuteCpuTask());
    }

    @Override
    public void update() {
        super.update();

        lastAction = currentAction;
        currentAction = Action.IDLE;

        lastConsoleMessagesBuffer = new ArrayList<>(consoleMessagesBuffer);
        consoleMessagesBuffer.clear();

        for (HardwareModule module : hardwareAddresses.values()) {
            module.update();
        }

        //Self-destroy when age limit is reached
        if (getAge() >= NonPlayerCharacter.LIFETIME) {
            setDead(true);
        }

        //Don't bother calling checkCompleted()
        getTask().tick(this);
    }

    @Override
    public void setKeyboardBuffer(ArrayList<Integer> kbBuffer) {
        LogManager.LOGGER.warning("Something went wrong here: Hacked NPC has no keyboard module" +
                "@HackedNPC::setKeyBoardBuffer()");
        Thread.dumpStack();
    }

    @Override
    public void setParent(User user) {
        LogManager.LOGGER.warning("Something went wrong here: Hacked NPC has no parent" +
                "@HackedNPC::setParent()");
        Thread.dumpStack();
    }

    @Override
    public User getParent() {
        LogManager.LOGGER.warning("Something went wrong here: Hacked NPC has no parent" +
                "@HackedNPC::getParent()");
        Thread.dumpStack();
        return null;
    }

    @Override
    public ArrayList<Integer> getKeyboardBuffer() {
        LogManager.LOGGER.warning("Something went wrong here: Hacked NPC has no keyboard module" +
                "@HackedNPC::getKeyBoardBuffer()");
        Thread.dumpStack();
        return null;
    }

    @Override
    public Memory getFloppyData() {
        LogManager.LOGGER.warning("Something went wrong here: Hacked NPC has floppy data." +
                "@HackedNPC::getFloppyData()");
        Thread.dumpStack();
        return null;
    }

    @Override
    public void setAction(Action action) {
        currentAction = action;
    }

    @Override
    public ArrayList<char[]> getConsoleMessagesBuffer() {
        return lastConsoleMessagesBuffer;
    }

    @Override
    public int getConsoleMode() {
        LogManager.LOGGER.warning("Something went wrong here: Hacked NPC has no console UI." +
                "@HackedNPC::getConsoleMode()");
        Thread.dumpStack();
        return 0;
    }

    @Override
    public CPU getCpu() {
        return cpu;
    }

    @Override
    public void giveItem(Item item) {
        //Overwrite item at current position
        ((NpcInventory) getHardware(NpcInventory.class)).putItem(item);
    }

    @Override
    public void attachHardware(HardwareModule hardware, int address) {
        hardwareAddresses.put(address, hardware);
        hardwareModules.put(hardware.getClass(), address);
    }

    @Override
    public void detachHardware(int address) {
        hardwareAddresses.remove(address);

        Class<? extends HardwareModule> toRemove = null;
        for (Class<? extends HardwareModule> clazz : hardwareModules.keySet()) {
            if (hardwareModules.get(clazz) == address) {
                toRemove = clazz;
            }
        }
        hardwareModules.remove(toRemove);
    }

    @Override
    public boolean hardwareInterrupt(int address, Status status) {
        HardwareModule hardware = hardwareAddresses.get(address);

        if (hardware != null) {
            hardware.handleInterrupt(status);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hardwareQuery(int address) {
        HardwareModule hardware = hardwareAddresses.get(address);

        if (hardware != null) {
            return hardware.getId();
        } else {
            return 0;
        }
    }

    public int getEnergy() {
        NpcBattery battery = (NpcBattery) getHardware(NpcBattery.class);
        return battery.getEnergy();
    }

    public void setEnergy(int energy) {
        NpcBattery battery = (NpcBattery) getHardware(NpcBattery.class);
        battery.setEnergy(energy);

        if (energy == 0 && DIE_ON_NO_ENERGY) {
            setDead(true);
        }
    }

    public boolean spendEnergy(int amount) {

        NpcBattery battery = (NpcBattery) getHardware(NpcBattery.class);

        if (battery.getEnergy() - amount < 0) {
            if (DIE_ON_NO_ENERGY) {
                setDead(true);
            }
            return false;
        } else {
            battery.setEnergy(battery.getEnergy() - amount);
            return true;
        }
    }

    @Override
    public Document mongoSerialise() {
        Document dbObject = super.mongoSerialise();

        dbObject.put("direction", getDirection().ordinal());
        dbObject.put("hp", getHp());
        dbObject.put("action", lastAction.ordinal());

        List<Document> hardwareList = new ArrayList<>();

        for (Integer address : hardwareAddresses.keySet()) {

            HardwareModule hardware = hardwareAddresses.get(address);

            Document serialisedHw = hardware.mongoSerialise();
            serialisedHw.put("address", address);
            hardwareList.add(serialisedHw);
        }

        dbObject.put("hardware", hardwareList);

        dbObject.put("memory", cpu.getMemory().mongoSerialise());

        dbObject.put("registerSet", cpu.getRegisterSet().mongoSerialise());
        return dbObject;
    }

    public void storeEnergy(int amount) {

        NpcBattery battery = (NpcBattery) getHardware(NpcBattery.class);
        battery.setEnergy(Math.min(battery.getEnergy() + amount, battery.getMaxEnergy()));
    }

    private HardwareModule getHardware(Class<? extends HardwareModule> clazz) {
        return hardwareAddresses.get(hardwareModules.get(clazz));
    }

    public void setMaxEnergy(int maxEnergy) {
        NpcBattery battery = (NpcBattery) getHardware(NpcBattery.class);
        battery.setMaxEnergy(maxEnergy);
    }

    public int getMaxEnergy() {
        NpcBattery battery = (NpcBattery) getHardware(NpcBattery.class);
        return battery.getMaxEnergy();
    }

    @Override
    public boolean sendMessage(char[] message) {
        return false;
    }

    @Override
    public void setCurrentAction(Action action) {
        currentAction = action;
    }

    @Override
    public Action getCurrentAction() {
        return currentAction;
    }

    @Override
    public JSONObject jsonSerialise() {
        JSONObject json = super.jsonSerialise();

        for (HardwareModule module : hardwareAddresses.values()) {
            JSONObject hwJson = module.jsonSerialise();
            if (hwJson != null) {
                json.put(module.getClass().getName(), hwJson);
            }
        }

        json.put("direction", getDirection().ordinal());
        NpcInventory inv = (NpcInventory) getHardware(NpcInventory.class);
        Item item = inv.getItem();
        json.put("heldItem", item == null ? new ItemVoid().getId() : item.getId());
        json.put("hp", getHp());
        json.put("action", lastAction.ordinal());

        return json;
    }

    @Override
    public boolean onDeadCallback() {

        getWorld().decUpdatable();

        if (getSettlement() != null && getSettlement().getNpcs() != null) {
            getSettlement().getNpcs().remove(this);
        }

        GameServer.INSTANCE.getEventDispatcher().dispatch(new ObjectDeathEvent(this));

        return false;
    }

    @Override
    public JSONObject debugJsonSerialise() {
        return jsonSerialise();
    }
}
