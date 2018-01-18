package net.simon987.server.assembly.instruction;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.Memory;
import net.simon987.server.assembly.Status;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AndInstructionTest {
    @Test
    public void executeTargetTarget() {

        ServerConfiguration config = new ServerConfiguration("config.properties");
        int memorySize = config.getInt("memory_size");

        //Memory
        Memory memory = new Memory(memorySize);
        memory.clear();

        //Status
        Status status = new Status();
        status.clear();

        AndInstruction andInstruction = new AndInstruction();

        //mem mem
        memory.clear();
        memory.set(0, 0xF010);
        memory.set(1, 0xF111);
        andInstruction.execute(memory, 0, memory, 1, status);

        assertEquals(0xF010, memory.get(0));
        assertEquals(true, status.isSignFlag());
        assertEquals(false, status.isZeroFlag());
        assertEquals(false, status.isOverflowFlag());
        assertEquals(false, status.isCarryFlag());

        //mem mem
        memory.clear();
        memory.set(0, 0x1010);
        memory.set(1, 0x0101);
        andInstruction.execute(memory, 0, memory, 1, status);

        assertEquals(0, memory.get(0));
        assertEquals(false, status.isSignFlag());
        assertEquals(true, status.isZeroFlag());
        assertEquals(false, status.isOverflowFlag());
        assertEquals(false, status.isCarryFlag());

    }

    @Test
    public void executeTargetImm() {

        ServerConfiguration config = new ServerConfiguration("config.properties");
        int memorySize = config.getInt("memory_size");

        //Memory
        Memory memory = new Memory(memorySize);
        memory.clear();

        //Status
        Status status = new Status();
        status.clear();

        AndInstruction andInstruction = new AndInstruction();

        //mem imm
        memory.clear();
        memory.set(0, 0x1010);
        andInstruction.execute(memory, 0, 0x0101, status);

        assertEquals(0, memory.get(0));
        assertEquals(false, status.isSignFlag());
        assertEquals(true, status.isZeroFlag());
        assertEquals(false, status.isOverflowFlag());
        assertEquals(false, status.isCarryFlag());
    }

}