package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;

/**
 * Created by simon on 02/06/17.
 */
public class RetInstruction extends Instruction {

    /**
     * Opcode of the instruction
     */
    public static final int OPCODE = 22;

    private CPU cpu;

    public RetInstruction(CPU cpu) {
        super("ret", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Status status) {
        cpu.setIp((char) cpu.getMemory().get(cpu.getRegisterSet().get(7))); //Jmp
        cpu.getRegisterSet().set(7, cpu.getRegisterSet().get(7) + 1); //Inc SP

        return status;
    }

    @Override
    public Status execute(int src, Status status) {
        cpu.setIp((char) cpu.getMemory().get(cpu.getRegisterSet().get(7))); //Jmp
        cpu.getRegisterSet().set(7, cpu.getRegisterSet().get(7) + src + 1); //Inc SP

        return status;
    }
}