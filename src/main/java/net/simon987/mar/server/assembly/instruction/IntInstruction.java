package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.*;

/**
 *  Software Interrupt
 *  Pushes the flags register and the IP to the stack then
 *  Sets the IP to the CPU [ivt_offset + src].
 */
public class IntInstruction extends Instruction {

    public static final int OPCODE = 48;
    private final CPU cpu;

    public static final int INT_DIVISION_BY_ZERO = 0;
    public static final int INT_INTO_DETECTED_OVERFLOW = 4;
    public static final int INT_EXEC_LIMIT_REACHED = 32;

    public IntInstruction(CPU cpu) {
        super("int", OPCODE);
        this.cpu = cpu;
    }

    @Override
    public Status execute(int src, Status status) {
        cpu.interrupt(src);
        return status;
    }

    @Override
    public boolean operandsValid(Operand o1, Operand o2) {
        return false;
    }

    @Override
    public boolean operandValid(Operand o1) {
        return o1.getType() == OperandType.IMMEDIATE16 && o1.getData() < 256;
    }
}
