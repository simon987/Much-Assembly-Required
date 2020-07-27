package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;

public class JgInstruction extends Instruction {

    public static final int OPCODE = 15;

    private final CPU cpu;

    public JgInstruction(CPU cpu) {
        super("jg", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {
        if (status.isSignFlag() == status.isOverflowFlag() && !status.isZeroFlag()) {
            cpu.setIp((char) src.get(srcIndex));
        }
        return status;
    }

    @Override
    public Status execute(int src, Status status) {
        if (status.isSignFlag() == status.isOverflowFlag() && !status.isZeroFlag()) {
            cpu.setIp((char) src);
        }
        return status;
    }
}
