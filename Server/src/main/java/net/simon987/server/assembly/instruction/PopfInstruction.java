package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Register;
import net.simon987.server.assembly.Status;

/**
 * Pops a single word off the top of the stack and sets the CPU flags to it.
 */
public class PopfInstruction extends Instruction {

    public static final int OPCODE = 44;

    private CPU cpu;

    public PopfInstruction(CPU cpu) {
        super("popf", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Status status) {

        Register sp = cpu.getRegisterSet().getRegister("SP");

        // Get the word on the top of the stack
        char flags = (char) cpu.getMemory().get(sp.getValue());

        // Overwrite the CPU flags
        status.fromByte(flags);

        // Increment SP
        sp.setValue(sp.getValue() + 1);
        
        return status;
    }

    public boolean noOperandsValid() {
        return true;
    }
}