package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

public class HwqInstruction extends Instruction {

    private static final int OPCODE = 28;

    private CPU cpu;

    public HwqInstruction(CPU cpu) {
        super("hwq", OPCODE);
        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {
        cpu.getHardwareHost().hardwareQuery(src.get(srcIndex));

        return status;
    }

    @Override
    public Status execute(int src, Status status) {
        cpu.getHardwareHost().hardwareQuery(src);

        return status;
    }
}
