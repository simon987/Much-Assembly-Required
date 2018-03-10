package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

/**
 * Jump if above
 */
public class JaInstruction extends Instruction {

    public static final int OPCODE = 46;

    private CPU cpu;

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
