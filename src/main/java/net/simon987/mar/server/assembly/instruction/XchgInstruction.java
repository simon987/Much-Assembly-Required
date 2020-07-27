package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;


/**
 * Swap operands. Does not alter the flags
 */
public class XchgInstruction extends Instruction {

    public static final int OPCODE = 31;

    private final CPU cpu;

    public XchgInstruction(CPU cpu) {
        super("xchg", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        int tmp = dst.get(dstIndex);
        dst.set(dstIndex, src.get(srcIndex));
        src.set(srcIndex, tmp);

        return status;
    }
}
