package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;

public class JzInstruction extends Instruction {

    /**
     * Opcode of the instruction
     */
    public static final int OPCODE = 14;

    private final CPU cpu;

    public JzInstruction(CPU cpu) {
        super("jz", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {
        if (status.isZeroFlag()) {
            cpu.setIp((char) src.get(srcIndex));
        }
        return status;
    }

    @Override
    public Status execute(int src, Status status) {
        if (status.isZeroFlag()) {
            cpu.setIp((char) src);
        }
        return status;
    }
}
