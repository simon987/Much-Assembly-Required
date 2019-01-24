package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Register;
import net.simon987.server.assembly.RegisterSet;
import net.simon987.server.assembly.Status;

import org.junit.Test;

import static org.junit.Assert.*;


public class SetneInstructionTest {
    private RegisterSet registers;
    private Status status;
    private SetccInstruction instruction;
    private int SETCCOPCODE = SetccInstruction.SETNE;

    public SetneInstructionTest() {
        registers = new RegisterSet();
        registers.put(1, new Register("R"));
        registers.clear();

        status = new Status();
        status.clear();

        instruction = new SetneInstruction();
    }
    
    /**
     * SETNE, SETNZ       Not Equal, Not Zero                  ZF=0
     */
    @Test
    public void execution() {
        status.setZeroFlag(true);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 0);

        status.setZeroFlag(false);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 1);
    }
}
