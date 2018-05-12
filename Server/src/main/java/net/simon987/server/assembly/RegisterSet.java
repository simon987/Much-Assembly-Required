package net.simon987.server.assembly;


import net.simon987.server.io.MongoSerializable;
import net.simon987.server.logging.LogManager;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A set of registers for a CPU
 */
public class RegisterSet implements Target, MongoSerializable {

    /**
     * List of registers
     */
    private HashMap<Integer, Register> registers = new HashMap<>(8);


    /**
     * Create an empty Register set
     */
    public RegisterSet() {

    }

    /**
     * Get the index of a Register by its name
     * This method assumes that the
     *
     * @param name Name of the register
     * @return index of the register, -1 if not found
     */
    int getIndex(String name) {

        name = name.toUpperCase();

        for (Integer i : registers.keySet()) {
            if (registers.get(i).getName().equals(name)) {

                return i;
            }
        }

        return -1;
    }

    /**
     * Get a register by its index
     *
     * @param index index of the register
     */
    public Register getRegister(int index) {
        return registers.get(index);
    }

    /**
     * Get a register by its name (e.g. "AX")
     *
     * @param name Name of the register, case insensitive
     */
    public Register getRegister(String name) {

        name = name.toUpperCase();

        for (Register r : registers.values()) {
            if (r.getName().equals(name)) {
                return r;
            }
        }

        return null;
    }

    /**
     * Get the value of a register
     *
     * @param address Address of the value. Can refer to a memory address or the index
     *                of a register
     * @return 16-bit value of a register
     */
    @Override
    public int get(int address) {

        Register register = registers.get(address);

        if (register != null) {
            return register.getValue();
        } else {
            return 0;
        }

    }

    /**
     * Set the value of a register
     *
     * @param address index of the value to change
     * @param value   value to set
     */
    @Override
    public void set(int address, int value) {

        Register register = registers.get(address);

        if (register != null) {
            register.setValue(value);
        } else {
            LogManager.LOGGER.info("DEBUG: trying to set unknown reg index : " + address);
        }
    }

    public void put(int index, Register register) {
        registers.put(index, register);
    }

    public void clear() {
        for (Register register : registers.values()) {
            register.setValue(0);
        }
    }

    /**
     * Add a register to the register set
     * <p>
     * the register set will break if the indexes of the registers
     * are not consecutive, starting at address 1.
     *
     * @param index Index of the register
     * @param reg   Register to add
     */
    void addRegister(int index, Register reg) {
        registers.put(index, reg);
    }

    int size() {
        return registers.size();
    }


    @Override
    public Document mongoSerialise() {
        List<Document> registers = new ArrayList<>();
        for (Integer index : this.registers.keySet()) {
            Document register = new Document();

            register.put("index", index);
            register.put("name", getRegister(index).getName());
            register.put("value", (int) getRegister(index).getValue());

            registers.add(register);
        }

        Document obj = new Document();
        obj.put("registers", registers);

        return obj;
    }

    public static RegisterSet deserialize(Document obj) {

        RegisterSet registerSet = new RegisterSet();

        List registers = (ArrayList) obj.get("registers");

        for (Object sRegister : registers) {

            Register register = new Register((String) ((Document) sRegister).get("name"));
            register.setValue(((Document) sRegister).getInteger("value"));

            registerSet.registers.put(((Document) sRegister).getInteger("index"), register);

        }

        return registerSet;
    }

    public static RegisterSet deserialize(JSONObject json) {

        RegisterSet registerSet = new RegisterSet();

        JSONArray registers = (JSONArray) json.get("registers");

        for (JSONObject jsonRegister : (ArrayList<JSONObject>) registers) {

            Register register = new Register((String) jsonRegister.get("name"));
            register.setValue((int) (long) jsonRegister.get("value"));

            registerSet.registers.put((int) (long) jsonRegister.get("index"), register);

        }

        return registerSet;
    }

    @Override
    public String toString() {
        String str = "";

        for (Integer index : registers.keySet()) {
            str += index + " " + registers.get(index).getName() + "=" + Util.toHex(registers.get(index).getValue()) + "\n";
        }

        return str;
    }
}
