package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

public class SarInstruction extends Instruction {

    private static final int OPCODE = 41;

    public SarInstruction() {
        super("sar", OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        int count = src.get(srcIndex) % 16;

        int destination = dst.get(dstIndex);

        if (count == 1) {
            status.setOverflowFlag(false); //sign doesn't change
        }

        if ((destination & 0x8000) == 0x8000) {
            destination |= 0xFFFF0000;
        }

        destination = destination >> (count - 1);
        status.setCarryFlag((destination & 1) == 1);
        destination = destination >> 1;
        status.setZeroFlag(destination == 0);

        dst.set(dstIndex, destination);

        return status;

    }

    @Override
    public Status execute(Target dst, int dstIndex, int src, Status status) {
        int count = src % 16;

        int destination = dst.get(dstIndex);

        if (count == 1) {
            status.setOverflowFlag(false); //sign doesn't change
        }

        if ((destination & 0x8000) == 0x8000) {
            destination |= 0xFFFF0000;
        }

        destination = destination >> (count - 1);
        status.setCarryFlag((destination & 1) == 1);
        destination = destination >> 1;
        status.setZeroFlag(destination == 0);

        dst.set(dstIndex, destination);

        return status;
    }
}
