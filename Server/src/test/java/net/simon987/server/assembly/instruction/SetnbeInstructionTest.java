package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Register;
import net.simon987.server.assembly.RegisterSet;
import net.simon987.server.assembly.Status;

import org.junit.Test;

import static org.junit.Assert.*;


public class SetnbeInstructionTest {
    private RegisterSet registers;
    private Status status;
    private SetccInstruction instruction;
    private int SETCCOPCODE = SetccInstruction.SETA;

    public SetnbeInstructionTest() {
        registers = new RegisterSet();
        registers.put(1, new Register("R"));
        registers.clear();

        status = new Status();
        status.clear();

        instruction = new SetnbeInstruction();
    }
    
    /**
     * SETA, SETNBE       Above, Not Below or Equal            CF=0 AND ZF=0
     */
    @Test
    public void execution() {
        status.setCarryFlag(false);
        status.setZeroFlag(false);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 1);
        
        status.setCarryFlag(true);
        status.setZeroFlag(false);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 0);

        status.setCarryFlag(false);
        status.setZeroFlag(true);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 0);

        status.setCarryFlag(true);
        status.setZeroFlag(true);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 0);
    }
}
