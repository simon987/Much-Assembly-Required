package net.simon987.server.assembly;

import net.simon987.server.ServerConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;

public class MemoryTest {
    
    ServerConfiguration getConfig() {
        String filePath = "config.properties";

        if (!new File(filePath).exists()) {
            File fallback = new File("Server/src/main/resources/", filePath);
            if (fallback.exists()) {
                filePath = fallback.getAbsolutePath();
            } else {
                throw new AssertionError("'config.properties' and 'Server/src/main/resources/config.properties' cannot be found with working directory: " + new File("").getAbsolutePath());
            }
        }

        ServerConfiguration config = new ServerConfiguration(filePath);
        return config;
    }

    @Test
    public void getSet() {
        int memorySize = getConfig().getInt("memory_size");
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
        int memorySize = getConfig().getInt("memory_size");
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