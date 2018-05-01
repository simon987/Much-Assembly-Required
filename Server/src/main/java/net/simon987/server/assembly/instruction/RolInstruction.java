package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

/**
 * <br>                +-----------------+
 * <br>                |                 |
 * <br> {@literal CF < 0<0<0<0<0<0<0<0 <-+ @}
 */
public class RolInstruction extends Instruction {

    private static final int OPCODE = 35;

    public RolInstruction() {
        super("rol", OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        int count = src.get(srcIndex) % 16;

        int destination = dst.get(dstIndex);
        int signBit = (destination & 0x8000);

        destination = (destination << count) | (destination >>> (16 - count));
        if (count == 1) {
            status.setOverflowFlag((destination & 0x8000) != signBit); //Set OF if sign bit changed
        }
        status.setCarryFlag((destination & 1) == 1);
        dst.set(dstIndex, destination);


        return status;
    }

    @Override
    public Status execute(Target dst, int dstIndex, int src, Status status) {

        int count = src % 16;
        int destination = dst.get(dstIndex);
        int signBit = (destination & 0x8000);

        destination = (destination << count) | (destination >>> (16 - count));
        if (count == 1) {
            status.setOverflowFlag((destination & 0x8000) != signBit); //Set OF if sign bit changed
        }
        status.setCarryFlag((destination & 1) == 1);
        dst.set(dstIndex, destination);


        return status;
    }
}
