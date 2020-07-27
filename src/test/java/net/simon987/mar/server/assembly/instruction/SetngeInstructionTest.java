package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.Register;
import net.simon987.mar.server.assembly.RegisterSet;
import net.simon987.mar.server.assembly.Status;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class SetngeInstructionTest {
    private final RegisterSet registers;
    private final Status status;
    private final SetccInstruction instruction;
    private final int SETCCOPCODE = SetccInstruction.SETL;

    public SetngeInstructionTest() {
        registers = new RegisterSet();
        registers.put(1, new Register("R"));
        registers.clear();

        status = new Status();
        status.clear();

        instruction = new SetngeInstruction();
    }

    /**
     * SETL, SETNGE       Less, Not Greater or Equal           SF<>OF
     */
    @Test
    public void execution() {
        status.setSignFlag(true);
        status.setOverflowFlag(false);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 1);

        status.setSignFlag(false);
        status.setOverflowFlag(true);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 1);

        status.setSignFlag(false);
        status.setOverflowFlag(false);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 0);

        status.setSignFlag(true);
        status.setOverflowFlag(true);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 0);
    }
}
