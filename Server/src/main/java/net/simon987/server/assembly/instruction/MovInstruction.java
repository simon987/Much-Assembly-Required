package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

/**
 * The MOV instruction copies data from a source to a destination.
 * it overwrites the value in the destination. The destination can be
 * memory or register, while the source can be an immediate value, memory
 * or register.
 * <p>
 * The instruction might have unexpected behavior if the an
 * operand of type [register + displacement] is used and its sum
 * is greater than 0xFFFF (It will overflow)
 * </p>
 * <p>
 * The MOV instruction doesn't change any flags
 * </p>
 */
public class MovInstruction extends Instruction {

    public static final int OPCODE = 1;

    public MovInstruction() {
        super("mov", OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        dst.set(dstIndex, src.get(srcIndex));

        return status;
    }

    @Override
    public Status execute(Target dst, int dstIndex, int src, Status status) {

        dst.set(dstIndex, src);


        return status;
    }

}
