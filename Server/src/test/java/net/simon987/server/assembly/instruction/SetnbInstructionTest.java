package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Register;
import net.simon987.server.assembly.RegisterSet;
import net.simon987.server.assembly.Status;

import org.junit.Test;

import static org.junit.Assert.*;


public class SetnbInstructionTest {
    private RegisterSet registers;
    private Status status;
    private SetccInstruction instruction;
    private int SETCCOPCODE = SetccInstruction.SETAE;

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
