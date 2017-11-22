package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

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
