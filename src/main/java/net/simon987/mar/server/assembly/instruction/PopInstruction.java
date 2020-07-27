package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;

/**
 * Created by simon on 02/06/17.
 */
public class PopInstruction extends Instruction {

    public static final int OPCODE = 20;

    private final CPU cpu;

    public PopInstruction(CPU cpu) {
        super("pop", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Target dst, int dstIndex, Status status) {

        dst.set(dstIndex, cpu.getMemory().get(cpu.getRegisterSet().getRegister("SP").getValue()));
        cpu.getRegisterSet().getRegister("SP").setValue(cpu.getRegisterSet().getRegister("SP").getValue() + 1); //Increment SP (stack grows towards smaller)

        return status;
    }
}
