package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;

/**
 * Send hardware interrupt
 * <br>Used to interact with the World using hardware
 */
public class HwiInstruction extends Instruction {

    public static final int OPCODE = 9;

    private final CPU cpu;

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
