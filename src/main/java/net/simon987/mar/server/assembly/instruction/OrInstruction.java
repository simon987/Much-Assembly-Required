package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;
import net.simon987.mar.server.assembly.Util;

public class OrInstruction extends Instruction {

    public static final int OPCODE = 5;

    public OrInstruction() {
        super("or", OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        int a = (char) dst.get(dstIndex);
        int b = (char) src.get(srcIndex);


        int result = (a | b);

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


        int result = (a | b);

        status.setSignFlag(Util.checkSign16(result));
        status.setZeroFlag((char) result == 0);
        status.setOverflowFlag(false);
        status.setCarryFlag(false);

        dst.set(dstIndex, result);

        return status;
    }
}
