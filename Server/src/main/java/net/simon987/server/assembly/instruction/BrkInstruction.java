package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;

/**
 * BRK (Break) Instruction. Will set the break flag and stop the CPU
 * execution
 */
public class BrkInstruction extends Instruction {

    public static final int OPCODE = 0;

    public BrkInstruction() {
        super("brk", OPCODE);
    }

    @Override
    public Status execute(Status status) {

        status.setBreakFlag(true);

        return status;
    }

    public boolean noOperandsValid() {
        return true;
    }
}
