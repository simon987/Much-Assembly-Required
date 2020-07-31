package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.Register;
import net.simon987.mar.server.assembly.RegisterSet;
import net.simon987.mar.server.assembly.Status;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class SetnbInstructionTest {
    private final RegisterSet registers;
    private final Status status;
    private final SetccInstruction instruction;
    private final int SETCCOPCODE = SetccInstruction.SETAE;

    public SetnbInstructionTest() {
        registers = new RegisterSet();
        registers.put(1, new Register("R"));
        registers.clear();

        status = new Status();
        status.clear();

        instruction = new SetnbInstruction();
    }

    /**
     * SETAE,SETNB,SETNC  Above or Equal, Not Below, No Carry  CF=0
     */
    @Test
    public void execution() {
        status.setCarryFlag(false);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 1);

        status.setCarryFlag(true);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 0);
    }
}