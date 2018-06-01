package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

/**
 * Send hardware interrupt
 * <br>Used to interact with the World using hardware
 *
 */
public class HwiInstruction extends Instruction {

    public static final int OPCODE = 9;

    private CPU cpu;

    public HwiInstruction(CPU cpu) {
        super("hwi", OPCODE);
        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {
        status.setErrorFlag(cpu.getHardwareHost().hardwareInterrupt(src.get(srcIndex), cpu.getStatus()));

        return status;
    }

    @Override
    public Status execute(int src, Status status) {

        status.setErrorFlag(cpu.getHardwareHost().hardwareInterrupt(src, cpu.getStatus()));

        return status;
    }


}
