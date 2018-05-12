package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;
import net.simon987.server.assembly.Util;

/**
 * AND two numbers together, the result is stored in the destination operand
 * <br>
 * AND A, B
 * <br>
 * {@literal A = A & B @}
 * <br>
 * FLAGS: OF=0 S=* Z=* X=0
 */
public class AndInstruction extends Instruction {

    /**
     * Opcode of the instruction
     */
    public static final int OPCODE = 4;

    public AndInstruction() {
        super("and", OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        int a = (char) dst.get(dstIndex);
        int b = (char) src.get(srcIndex);


        int result = (a & b);

        status.setSignFlag(Util.checkSign16(result));
        status.setZeroFlag((char) result == 0);
        status.setOverflowFlag(false);
        status.setCarryFlag(false);

        dst.set(dstIndex, result);

        return status;
    }

    @Override
    public Status execute(Target dst, int dstIndex, int src, Status status) {
        int a = (char) dst.get(dstIndex);
        int b = (char) src;


        int result = (a & b);

        status.setSignFlag(Util.checkSign16(result));
        status.setZeroFlag((char) result == 0);
        status.setOverflowFlag(false);
        status.setCarryFlag(false);

        dst.set(dstIndex, result);

        return status;
    }

}
