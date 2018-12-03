package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.*;

public class HwqInstruction extends Instruction {

    private static final int OPCODE = 28;

    private CPU cpu;
    private Register b;

    public HwqInstruction(CPU cpu) {
        super("hwq", OPCODE);
        this.cpu = cpu;
        this.b = cpu.getRegisterSet().getRegister("B");
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {
        b.setValue(cpu.getHardwareHost().hardwareQuery(src.get(srcIndex)));

        return status;
    }

    @Override
    public Status execute(int src, Status status) {
        b.setValue(cpu.getHardwareHost().hardwareQuery(src));

        return status;
    }

}
