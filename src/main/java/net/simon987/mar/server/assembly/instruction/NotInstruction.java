package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;

public class NotInstruction extends Instruction {

    public static final int OPCODE = 29;

    public NotInstruction() {
        super("not", OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Status status) {
        dst.set(dstIndex, ~dst.get(dstIndex));

        return status;
    }
}
