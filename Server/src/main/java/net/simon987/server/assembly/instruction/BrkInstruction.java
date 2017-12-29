package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;

/**
 * BRK (Break) Instruction. Will set the break flag and stop the CPU
 * execution
 */
public class BrkInstruction extends Instruction {

    public BrkInstruction() {
        super("brk", 0);
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
