package net.simon987.server.assembly;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.exception.CancelledException;
import net.simon987.server.user.User;
import org.junit.Test;

import java.util.Random;

public class CPUTest {

    @Test
    public void executeInstruction() throws CancelledException {

        ServerConfiguration config = new ServerConfiguration("config.properties");
        User user = new User();
        CPU cpu = new CPU(config, user);


        for(int i = 0 ; i < 3 ; i++){
            //Execute every possible instruction with random values in memory
            cpu.reset();
            cpu.getMemory().clear();
            Random random = new Random();
            byte[] randomBytes = new byte[cpu.getMemory().getWords().length * 2];
            random.nextBytes(randomBytes);

            for (int machineCode = Character.MIN_VALUE; machineCode < Character.MAX_VALUE; machineCode++) {
                Instruction instruction = cpu.getInstructionSet().get(machineCode & 0x03F); // 0000 0000 00XX XXXX

                int source = (machineCode >> 11) & 0x001F; // XXXX X000 0000 0000
                int destination = (machineCode >> 6) & 0x001F; // 0000 0XXX XX00 0000

                cpu.executeInstruction(instruction, source, destination);
            }
        }
    }

}