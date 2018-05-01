package net.simon987.server.assembly.instruction;


import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

/**
 *<br>            +---------------------+
 *<br>            |                     |
 *<br> {@literal CF < 0<0<0<0<0<0<0<0 <-+  @}
 */
public class RclInstruction extends Instruction {

    private static final int OPCODE = 39;

    public RclInstruction() {
        super("rcl", OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        int count = src.get(srcIndex) % 17;

        int destination = dst.get(dstIndex);
        int signBit = (destination & 0x8000);

        if (status.isCarryFlag()) {
            destination |= 0x10000;
        }

        destination = (destination << count) | (destination >>> (17 - count));

        status.setCarryFlag((destination & 0x10000) == 0x10000);

        if (count == 1) {
            status.setOverflowFlag((destination & 0x8000) != signBit); //Set OF if sign bit changed
        }
        dst.set(dstIndex, destination & 0xFFFF);


        return status;
    }

    @Override
    public Status execute(Target dst, int dstIndex, int src, Status status) {

        int count = src % 17;

        int destination = dst.get(dstIndex);
        int signBit = (destination & 0x8000);

        if (status.isCarryFlag()) {
            destination |= 0x10000;
        }

        destination = (destination << count) | (destination >>> (17 - count));

        status.setCarryFlag((destination & 0x10000) == 0x10000);

        if (count == 1) {
            status.setOverflowFlag((destination & 0x8000) != signBit); //Set OF if sign bit changed
        }
        dst.set(dstIndex, destination & 0xFFFF);


        return status;
    }
}
