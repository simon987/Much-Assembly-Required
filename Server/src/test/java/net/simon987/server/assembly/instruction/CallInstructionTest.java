package net.simon987.server.assembly.instruction;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.*;
import net.simon987.server.user.User;
import org.junit.Test;

public class CallInstructionTest {


    @Test
    public void execute() throws Exception {

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

        CPU cpu = new CPU(config, new User());

        CallInstruction callInstruction = new CallInstruction(cpu);

        //We have to check if IP is 'pushed' correctly (try to pop it), the



    }

    @Test
    public void execute1() throws Exception {
    }

}