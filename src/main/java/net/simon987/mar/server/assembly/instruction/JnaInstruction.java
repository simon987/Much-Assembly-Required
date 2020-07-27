package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;

/**
 * Jump if not above
 */
public class JnaInstruction extends Instruction {

    public static final int OPCODE = 47;

    private final CPU cpu;

    public JnaInstruction(CPU cpu) {
        super("jna", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {
        if (status.isCarryFlag() || status.isZeroFlag()) {
            cpu.setIp((char) src.get(srcIndex));
        }
        return status;
    }

    @Override
    public Status execute(int src, Status status) {
        if (status.isCarryFlag() || status.isZeroFlag()) {
            cpu.setIp((char) src);
        }
        return status;
    }
}
