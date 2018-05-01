package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

/**
 * <br>            +---------------------+
 * <br>            |                     |
 * <br> {@literal CF < 0<0<0<0<0<0<0<0 <-+ @}
 */
public class RcrInstruction extends Instruction {

    private static final int OPCODE = 40;

    public RcrInstruction() {
        super("rcr", OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        int count = src.get(srcIndex) % 17;

        int destination = dst.get(dstIndex) << 1;
        int signBit = (destination & 0x10000);

        if (status.isCarryFlag()) {
            destination |= 1;
        }

        destination = (destination >>> count) | (destination << (17 - count));

        status.setCarryFlag((destination & 1) == 1);

        if (count == 1) {
            status.setOverflowFlag((destination & 0x10000) != signBit); //Set OF if sign bit changed
        }
        dst.set(dstIndex, destination >> 1);


        return status;
    }

    @Override
    public Status execute(Target dst, int dstIndex, int src, Status status) {

        int count = src % 17;

        int destination = dst.get(dstIndex) << 1;
        int signBit = (destination & 0x10000);

        if (status.isCarryFlag()) {
            destination |= 1;
        }

        destination = (destination >>> count) | (destination << (17 - count));

        status.setCarryFlag((destination & 1) == 1);

        if (count == 1) {
            status.setOverflowFlag((destination & 0x10000) != signBit); //Set OF if sign bit changed
        }
        dst.set(dstIndex, destination >> 1);


        return status;
    }
}
