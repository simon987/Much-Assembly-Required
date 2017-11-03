package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;

/**
 * Send hardware interupt
 * Used to interact with the World using hardware
 * </p>
 */
public class HwiInstruction extends Instruction {

    public static final int OPCODE = 9;

    private CPU cpu;

    public HwiInstruction(CPU cpu) {
        super("hwi", OPCODE);
        this.cpu = cpu;
    }

    @Override
    public Status execute(int src, Status status) {

        status.setErrorFlag(cpu.hardwareInterrupt(src));

        return status;
    }


}
