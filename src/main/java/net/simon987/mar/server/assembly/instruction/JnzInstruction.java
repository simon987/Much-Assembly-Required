package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;

public class JnzInstruction extends Instruction {

    public static final int OPCODE = 13;

    private final CPU cpu;

    public JnzInstruction(CPU cpu) {
        super("jnz", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {
        if (!status.isZeroFlag()) {
            cpu.setIp((char) src.get(srcIndex));
        }
        return status;
    }

    @Override
    public Status execute(int src, Status status) {
        if (!status.isZeroFlag()) {
            cpu.setIp((char) src);
        }
        return status;
    }
}
