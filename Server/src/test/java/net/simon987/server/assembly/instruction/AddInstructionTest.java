package net.simon987.server.assembly.instruction;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.Memory;
import net.simon987.server.assembly.Register;
import net.simon987.server.assembly.RegisterSet;
import net.simon987.server.assembly.Status;
import org.junit.Test;

import static org.junit.Assert.*;


public class AddInstructionTest {


    /**
     * ADD mem/reg, mem/reg
     */
    @Test
    public void addTargetTarget() {
        ServerConfiguration config = new ServerConfiguration("config.properties");
        int memorySize = config.getInt("memory_size");

        //Memory
        Memory memory = new Memory(memorySize);
        memory.clear();

        //RegisterSet
        RegisterSet registerSet = new RegisterSet();
        registerSet.put(1, new Register("T1"));
        registerSet.put(2, new Register("T2"));
        registerSet.clear();

        //Status
        Status status = new Status();
        status.clear();

        AddInstruction addInstruction = new AddInstruction();

        //ADD mem, mem
        //Positive numbers
        memory.set(0, 10);
        memory.set(1, 10);
        addInstruction.execute(memory, 0, memory, 1, status);
        assertEquals(20, memory.get(0));
        assertEquals(10, memory.get(1));
        assertEquals(10, memory.get(1));
        //FLAGS Should be CF=0 ZF=0 SF=0 OF=0
        assertFalse(status.isSignFlag());
        assertFalse(status.isZeroFlag());
        assertFalse(status.isCarryFlag());
        assertFalse(status.isOverflowFlag());
        assertFalse(status.isBreakFlag());

        memory.clear();
        memory.set(memorySize - 1, 10);
        memory.set(1, 10);
        addInstruction.execute(memory, memorySize - 1, memory, 1, status);
        assertEquals(20, memory.get(memorySize - 1));
        assertEquals(10, memory.get(1));
        //FLAGS Should be CF=0 ZF=0 SF=0 OF=0
        assertFalse(status.isSignFlag());
        assertFalse(status.isZeroFlag());
        assertFalse(status.isCarryFlag());
        assertFalse(status.isOverflowFlag());
        assertFalse(status.isBreakFlag());

        //Large positive numbers
        memory.clear();
        memory.set(0, 2);
        memory.set(1, 0xFFFF);
        addInstruction.execute(memory, memorySize, memory, 1, status);
        assertEquals(1, memory.get(0));
        assertEquals(0xFFFF, memory.get(1));
        //FLAGS Should be CF=1 ZF=0 SF=0 OF=0
        assertFalse(status.isSignFlag());
        assertFalse(status.isZeroFlag());
        assertTrue(status.isCarryFlag());
        assertFalse(status.isOverflowFlag());
        assertFalse(status.isBreakFlag());

        //Zero result
        memory.clear();
        memory.set(0, 1);
        memory.set(1, 0xFFFF);
        addInstruction.execute(memory, memorySize, memory, 1, status);
        assertEquals(0, memory.get(0));
        assertEquals(0xFFFF, memory.get(1));
        //FLAGS Should be CF=1 ZF=1 SF=0 OF=0
        assertTrue(status.isCarryFlag());
        assertTrue(status.isZeroFlag());
        assertFalse(status.isSignFlag());
        assertFalse(status.isOverflowFlag());
        assertFalse(status.isBreakFlag());

        //Overflow
        memory.clear();
        memory.set(0, 0x8000);
        memory.set(1, 0xFFFF);
        addInstruction.execute(memory, 0, memory, 1, status);
        assertEquals(0x7FFF, memory.get(0));
        assertEquals(0xFFFF, memory.get(1));
        //FLAGS Should be CF=1 ZF=0 SF=0 OF=1
        assertTrue(status.isCarryFlag());
        assertFalse(status.isZeroFlag());
        assertFalse(status.isSignFlag());
        assertTrue(status.isOverflowFlag());
        assertFalse(status.isBreakFlag());

        //ADD reg, reg
        //Positive numbers
        registerSet.set(1, 10);
        registerSet.set(2, 10);
        addInstruction.execute(registerSet, 1, registerSet, 2, status);
        assertEquals(20, registerSet.get(1));
        assertEquals(10, registerSet.get(2));
        //FLAGS Should be CF=0 ZF=0 SF=0 OF=0
        assertFalse(status.isSignFlag());
        assertFalse(status.isZeroFlag());
        assertFalse(status.isCarryFlag());
        assertFalse(status.isOverflowFlag());
        assertFalse(status.isBreakFlag());

    }


    /**
     * ADD mem/reg, imm
     */
    @Test
    public void addTargetImm() {
        ServerConfiguration config = new ServerConfiguration("config.properties");
        int memorySize = config.getInt("memory_size");

        //Memory
        Memory memory = new Memory(memorySize);
        memory.clear();

        //Status
        Status status = new Status();
        status.clear();

        AddInstruction addInstruction = new AddInstruction();

        //Positive number
        memory.clear();
        memory.set(0, 10);
        addInstruction.execute(memory, 0, 10, status);
        assertEquals(20, memory.get(0));
        //FLAGS Should be CF=0 ZF=0 SF=0 OF=0
        assertFalse(status.isSignFlag());
        assertFalse(status.isZeroFlag());
        assertFalse(status.isCarryFlag());
        assertFalse(status.isOverflowFlag());
        assertFalse(status.isBreakFlag());

        //The rest is assumed to work since it is tested with addTargetTarget() and behavior shouldn't be different;

    }

}