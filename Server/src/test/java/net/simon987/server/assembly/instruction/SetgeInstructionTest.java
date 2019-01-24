package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Register;
import net.simon987.server.assembly.RegisterSet;
import net.simon987.server.assembly.Status;

import org.junit.Test;

import static org.junit.Assert.*;


public class SetgeInstructionTest {
    private RegisterSet registers;
    private Status status;
    private SetccInstruction instruction;
    private int SETCCOPCODE = SetccInstruction.SETGE;

    public SetgeInstructionTest() {
        registers = new RegisterSet();
        registers.put(1, new Register("R"));
        registers.clear();

        status = new Status();
        status.clear();

        instruction = new SetgeInstruction();
    }
    
    /**
     * SETGE, SETNL       Greater or Equal, Not Less           SF=OF
     */
    @Test
    public void execution() {
        status.setSignFlag(true);
        status.setOverflowFlag(true);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 1);

        status.setSignFlag(true);
        status.setOverflowFlag(false);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 0);

        status.setSignFlag(false);
        status.setOverflowFlag(true);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 0);

        status.setSignFlag(false);
        status.setOverflowFlag(false);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 1);
    }
}
