package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;


/**
 * Swap operands. Does not alter the flags
 */
public class XchgInstruction extends Instruction {

    public static final int OPCODE = 31;

    private CPU cpu;

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
