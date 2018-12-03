package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;
import net.simon987.server.assembly.Util;

/**
 * Add two numbers together, the result is stored in the destination operand
 * <p>
 * ADD A, B
 * A = A + B
 * </p>
 */
public class AddInstruction extends Instruction {

    public static final int OPCODE = 2;

    public AddInstruction() {
        super("add", OPCODE);
    }

    private static Status add(int a, int b, Status status, Target dst, int dstIndex) {
        int result = a + b;

        status.setSignFlag(Util.checkSign16(result));
        status.setZeroFlag((char) result == 0);
        status.setOverflowFlag(Util.checkOverFlowAdd16(a, b));
        status.setCarryFlag(Util.checkCarry16(result));

        dst.set(dstIndex, result);

        return status;
    }

    @Override
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        int a = (char) dst.get(dstIndex);
        int b = (char) src.get(srcIndex);

        return add(a, b, status, dst, dstIndex);
    }

    @Override
    public Status execute(Target dst, int dstIndex, int src, Status status) {

        int a = (char) dst.get(dstIndex);
        int b = (char) src;

        return add(a, b, status, dst, dstIndex);
    }
}
