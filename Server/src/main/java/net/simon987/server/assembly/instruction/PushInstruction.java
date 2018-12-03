package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.*;

public class PushInstruction extends Instruction {

    public static final int OPCODE = 19;

    private CPU cpu;

    public PushInstruction(CPU cpu) {
        super("push", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {

        Register sp = cpu.getRegisterSet().getRegister("SP");

        sp.setValue(sp.getValue() - 1); //Decrement SP (stack grows towards smaller)
        cpu.getMemory().set(sp.getValue(), src.get(srcIndex));

        return status;
    }

    @Override
    public Status execute(int src, Status status) {
        Register sp = cpu.getRegisterSet().getRegister("SP");

        sp.setValue(sp.getValue() - 1); //Decrement SP (stack grows towards smaller)
        cpu.getMemory().set(sp.getValue(), src);

        return status;
    }
}