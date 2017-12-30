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
        //If the operand is zero, the carry flag is cleared; in all other cases, the carry flag is set.

        char destination = (char) dst.get(dstIndex);

        if (destination == 0) {
            status.setCarryFlag(false);
            status.setZeroFlag(true);
        } else {
            status.setCarryFlag(true);
        }

        //Attempting to negate a word containing -32,768 causes no change to the operand and sets the Overflow Flag.
        if (destination == 0x8000) {
            status.setOverflowFlag(true);
        } else {
            dst.set(dstIndex, -destination);
        }

        return status;
    }
}
