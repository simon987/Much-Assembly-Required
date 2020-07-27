package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Register;
import net.simon987.mar.server.assembly.Status;

/**
 * Pushes the current CPU flags onto the stack.
 */
public class PushfInstruction extends Instruction {

    public static final int OPCODE = 45;

    private final CPU cpu;

    public PushfInstruction(CPU cpu) {
        super("pushf", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Status status) {

        // Decrement SP
        Register sp = cpu.getRegisterSet().getRegister("SP");
        sp.setValue(sp.getValue() - 1);

        // Push the current flags
        cpu.getMemory().set(sp.getValue(), status.toByte());

        return status;
    }

    public boolean noOperandsValid() {
        return true;
    }
}
