package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

public class JzInstruction extends Instruction {

    /**
     * Opcode of the instruction
     */
    public static final int OPCODE = 14;

    private CPU cpu;

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
