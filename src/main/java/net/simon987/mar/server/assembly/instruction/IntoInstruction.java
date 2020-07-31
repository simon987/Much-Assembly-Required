package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.*;

public class IntoInstruction extends Instruction {

    public static final int OPCODE = 8;
    private final CPU cpu;


    public IntoInstruction(CPU cpu) {
        super("into", OPCODE);
        this.cpu = cpu;
    }

    @Override
    public Status execute(Status status) {
        if (status.isOverflowFlag()) {
            cpu.interrupt(IntInstruction.INT_INTO_DETECTED_OVERFLOW);
        }
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
