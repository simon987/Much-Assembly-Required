package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;
import net.simon987.server.assembly.Util;

public class ShrInstruction extends Instruction {

    public static final int OPCODE = 7;

    public ShrInstruction() {
        super("shr", OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        int a = (char) dst.get(dstIndex);
        int count = (char) src.get(srcIndex);

        int result = a >> count;

        if (Util.checkSign16(a) != Util.checkSign16(result)) {
            status.setOverflowFlag(true);
        }

        /*
        SHR 2 carry flag check example

        0000 0000 0000 0000 1111 1111 1111 1111 << 16
        1111 1111 1111 1111 0000 0000 0000 0000 >> 2
        0011 1111 1111 1111 1100 0000 0000 0000 & 0x8000 (15th bit)
        0000 0000 0000 0000 1000 0000 0000 0000
        carry flag is set
         */

        status.setCarryFlag((((a << 16) >> count) & 0x8000) != 0);
        status.setSignFlag(Util.checkSign16(result));
        status.setZeroFlag((char) result == 0);

        dst.set(dstIndex, result);

        return status;
    }

    @Override
    public Status execute(Target dst, int dstIndex, int src, Status status) {
        int a = (char) dst.get(dstIndex);
        int count = (char) src;

        int result = a >> count;

        if (Util.checkSign16(a) != Util.checkSign16(result)) {
            status.setOverflowFlag(true);
        }

        status.setCarryFlag((((a << 16) >> count) & 0x8000) != 0);
        status.setSignFlag(Util.checkSign16(result));
        status.setZeroFlag((char) result == 0);

        dst.set(dstIndex, result);

        return status;
    }
}
