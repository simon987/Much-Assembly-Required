package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;

/**
 * Jump if above
 */
public class JaInstruction extends Instruction {

    public static final int OPCODE = 46;

    private final CPU cpu;

    public JaInstruction(CPU cpu) {
        super("ja", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {
        if (!status.isCarryFlag() && !status.isZeroFlag()) {
            cpu.setIp((char) src.get(srcIndex));
        }
        return status;
    }

    @Override
    public Status execute(int src, Status status) {
        if (!status.isCarryFlag() && !status.isZeroFlag()) {
            cpu.setIp((char) src);
        }
        return status;
    }
}
