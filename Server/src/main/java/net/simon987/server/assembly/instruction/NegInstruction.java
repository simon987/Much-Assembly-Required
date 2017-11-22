package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

public class NegInstruction extends Instruction {

    public static final int OPCODE = 25;

    public NegInstruction() {
        super("neg", OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Status status) {
        dst.set(dstIndex, -dst.get(dstIndex));

        return status;
    }
}
