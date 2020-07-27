package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;

public class JncInstruction extends Instruction {

    private static final int OPCODE = 34;

    private final CPU cpu;

    public JncInstruction(CPU cpu) {
        super("jnc", OPCODE);
        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {
        if (!status.isCarryFlag()) {
            cpu.setIp((char) src.get(srcIndex));
        }
        return status;
    }

    @Override
    public Status execute(int src, Status status) {
        if (!status.isCarryFlag()) {
            cpu.setIp((char) src);
        }
        return status;
    }

}
