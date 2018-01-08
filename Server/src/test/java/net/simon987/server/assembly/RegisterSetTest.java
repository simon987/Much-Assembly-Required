package net.simon987.server.assembly;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegisterSetTest {
    @Test
    public void getIndex() {

        RegisterSet registerSet = new RegisterSet();

        Register r1 = new Register("R1");
        Register r2 = new Register("R2");

        registerSet.put(1, r1);
        registerSet.put(2, r2);

        assertEquals(1, registerSet.getIndex("R1"));
        assertEquals(2, registerSet.getIndex("R2"));

        assertEquals(-1, registerSet.getIndex("Unknown register name"));
    }

    @Test
    public void getRegister() {

        RegisterSet registerSet = new RegisterSet();

        Register r1 = new Register("R1");
        Register r2 = new Register("R2");

        registerSet.put(1, r1);
        registerSet.put(2, r2);

        assertEquals(r1, registerSet.getRegister("R1"));
        assertEquals(r1, registerSet.getRegister(1));

        assertEquals(r2, registerSet.getRegister("R2"));
        assertEquals(r2, registerSet.getRegister(2));

        //Test unknown registers
        assertEquals(null, registerSet.getRegister("Unknown"));
        assertEquals(null, registerSet.getRegister(3));
    }

    @Test
    public void get() {
        RegisterSet registerSet = new RegisterSet();

        Register r1 = new Register("R1");

        registerSet.put(1, r1);

        r1.setValue(10);
        assertEquals(10, registerSet.get(1));

        //Test unknown indexes
        assertEquals(0, registerSet.get(2));
    }

    @Test
    public void set() {

        RegisterSet registerSet = new RegisterSet();

        Register r1 = new Register("R1");

        registerSet.put(1, r1);
        registerSet.set(1, 10);

        assertEquals(10, r1.getValue());

        //Test unknown indexes
        registerSet.set(3, 10);
    }

}