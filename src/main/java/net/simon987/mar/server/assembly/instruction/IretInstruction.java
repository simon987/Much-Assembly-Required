package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.*;

/**
 * Interrupt Return
 *
 * Pops the IP and status flag from the stack.
 */
public class IretInstruction extends Instruction {

    public static final int OPCODE = 49;
    private final CPU cpu;

    public IretInstruction(CPU cpu) {
        super("iret", OPCODE);
        this.cpu = cpu;
    }

    public Status execute(Status status) {
        RegisterSet reg = cpu.getRegisterSet();

        cpu.setIp((char) cpu.getMemory().get(reg.get(7))); //IP = (SP + 0)
        status.fromWord((char) cpu.getMemory().get(reg.get(7) + 1)); //Status = (SP + 1)
        reg.set(7, reg.get(7) + 2); //Increment SP (stack grows towards smaller)
        return status;
    }

    @Override
    public boolean noOperandsValid() {
        return true;
    }

    @Override
    public boolean operandsValid(Operand o1, Operand o2) {
        return false;
    }

    @Override
    public boolean operandValid(Operand o1) {
        return false;
    }
}
