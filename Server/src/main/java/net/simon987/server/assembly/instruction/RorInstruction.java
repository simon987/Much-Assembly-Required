package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

/**
 * <br>           +-----------------+
 * <br>           |                 |
 * <br> {@literal +-> 0>0>0>0>0>0>0>0 > CF @}
 */
public class RorInstruction extends Instruction {

    private static final int OPCODE = 32;

    public RorInstruction() {
        super("ror", OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        int count = src.get(srcIndex) % 16;

        int destination = dst.get(dstIndex);
        int signBit = (destination & 0x8000);

        destination = (destination >>> count) | (destination << (16 - count));
        if (count == 1) {
            status.setOverflowFlag((destination & 0x8000) != signBit); //Set OF if sign bit changed
        }
        dst.set(dstIndex, destination);
        status.setCarryFlag((destination & 0x8000) == 0x8000);
        return status;
    }

    @Override
    public Status execute(Target dst, int dstIndex, int src, Status status) {

        int count = src % 16;
        int destination = dst.get(dstIndex);
        int signBit = (destination & 0x8000);

        destination = (destination >>> count) | (destination << (16 - count));
        if (count == 1) {
            status.setOverflowFlag((destination & 0x8000) != signBit); //Set OF if sign bit changed
        }
        dst.set(dstIndex, destination);
        status.setCarryFlag((destination & 0x8000) == 0x8000);
        return status;
    }
}
