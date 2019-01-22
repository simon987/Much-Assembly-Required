package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;
import net.simon987.server.assembly.Util;

public class ShlInstruction extends Instruction {

    public static final int OPCODE = 6;

    public ShlInstruction() {
        super("shl", OPCODE);
    }

    public ShlInstruction(String alias) {
        super(alias, OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        int a = (char) dst.get(dstIndex);
        int count = (char) src.get(srcIndex);

        int result = a << count;

        status.setOverflowFlag(Util.checkSign16(a) != Util.checkSign16(result));
        status.setCarryFlag(result >> 16 != 0);
        status.setSignFlag(Util.checkSign16(result));
        status.setZeroFlag((char) result == 0);

        dst.set(dstIndex, result);

        return status;
    }

    @Override
    public Status execute(Target dst, int dstIndex, int src, Status status) {
        int a = (char) dst.get(dstIndex);
        int count = (char) src;

        int result = a << count;

        status.setOverflowFlag(Util.checkSign16(a) != Util.checkSign16(result));
        status.setCarryFlag(result >> 16 != 0);
        status.setSignFlag(Util.checkSign16(result));
        status.setZeroFlag((char) result == 0);

        dst.set(dstIndex, result);

        return status;
    }
}
