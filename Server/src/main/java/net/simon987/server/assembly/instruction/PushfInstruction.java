package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Register;
import net.simon987.server.assembly.Status;

/**
 * Pushes the current CPU flags onto the stack.
 */
public class PushfInstruction extends Instruction {

    /**
     * Opcode of the instruction
     */
    public static final int OPCODE = 45;

    private CPU cpu;

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
