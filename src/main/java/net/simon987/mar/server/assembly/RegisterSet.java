package net.simon987.mar.server.assembly;


import net.simon987.mar.server.io.MongoSerializable;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of registers for a CPU
 */
public class RegisterSet implements Target, MongoSerializable, Cloneable {

    private int size = 0;

    private final Register[] registers = new Register[16];

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

        for (int i = 1; i <= size; i++) {
            if (registers[i].getName().equals(name)) {
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
        return registers[index];
    }

    /**
     * Get a register by its name (e.g. "AX")
     *
     * @param name Name of the register, case insensitive
     */
    public Register getRegister(String name) {

        name = name.toUpperCase();

        for (Register r : registers) {
            if (r == null) continue;
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
        if (address <= 0 || address > size) {
            return 0;
        }

        return registers[address].getValue();
    }

    /**
     * Set the value of a register
     *
     * @param address index of the value to change
     * @param value   value to set
     */
    @Override
    public void set(int address, int value) {
        if (address <= 0 || address > size) {
            return;
        }

        registers[address].setValue(value);
    }

    public void clear() {
        for (Register r : registers) {
            if (r == null) continue;
            r.setValue(0);
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
    public void put(int index, Register reg) {
        registers[index] = reg;
        size = Math.max(index, size);
    }

    int size() {
        return size;
    }


    @Override
    public Document mongoSerialise() {
        List<Document> registers = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            Document register = new Document();

            register.put("index", i);
            register.put("name", getRegister(i).getName());
            register.put("value", (int) getRegister(i).getValue());

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
            if (sRegister == null) continue;

            Register register = new Register((String) ((Document) sRegister).get("name"));
            register.setValue(((Document) sRegister).getInteger("value"));

            registerSet.put(((Document) sRegister).getInteger("index"), register);

        }

        return registerSet;
    }

    public static RegisterSet deserialize(JSONObject json) {

        RegisterSet registerSet = new RegisterSet();

        JSONArray registers = (JSONArray) json.get("registers");

        for (JSONObject jsonRegister : (ArrayList<JSONObject>) registers) {

            Register register = new Register((String) jsonRegister.get("name"));
            register.setValue((int) (long) jsonRegister.get("value"));

            registerSet.put((int) (long) jsonRegister.get("index"), register);

        }

        return registerSet;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i <= size; i++) {
            Register reg = getRegister(i);
            sb.append(reg.getName());
            sb.append("=");
            if (i == size) {
                sb.append(String.format("%04X", (int)reg.getValue()));
            } else {
                sb.append(String.format("%04X ", (int)reg.getValue()));
            }
        }

        return sb.toString();
    }

    @Override
    public RegisterSet clone() {
        RegisterSet rs = new RegisterSet();

        for (int i = 1; i <= size; i++) {
            rs.put(i, getRegister(i).clone());
        }
        return rs;
    }
}
