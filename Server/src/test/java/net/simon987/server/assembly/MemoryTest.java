package net.simon987.server.assembly;

import net.simon987.server.ServerConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;


public class MemoryTest {
    @Test
    public void getSet() {
        ServerConfiguration config = new ServerConfiguration("config.properties");
        int memorySize = config.getInt("memory_size");
        Memory memory = new Memory(memorySize);

        memory.set(1, 1);
        assertEquals(1, memory.get(1));

        memory.set(memorySize / 2 - 1, 1);
        assertEquals(1, memory.get(memorySize / 2 - 1));

        memory.get(memorySize / 2);
        memory.get(-1);

        memory.set(memorySize / 2, 1);
        memory.set(-1, 1);
    }

    @Test
    public void write() {

        ServerConfiguration config = new ServerConfiguration("config.properties");
        int memorySize = config.getInt("memory_size");
        Memory memory = new Memory(memorySize);


        assertTrue(memory.write(0, new char[memorySize], 0, memorySize));
        assertFalse(memory.write(0, new char[memorySize], 0, memorySize + 1));
        assertFalse(memory.write(0, new char[memorySize], 0, -1));
        assertFalse(memory.write(-1, new char[memorySize], 0, 10));

        assertFalse(memory.write(memorySize, new char[15], 0, 1));
        assertFalse(memory.write((memorySize) - 5, new char[11], 0, 6));
        assertTrue(memory.write((memorySize) - 5, new char[11], 0, 5));

    }


}