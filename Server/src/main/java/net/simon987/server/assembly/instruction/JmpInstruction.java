package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

/**
 * Created by Gilbert Fortier on 3/11/2017.
 */
public class JmpInstruction extends Instruction {

    /**
     * Opcode of the instruction
     */
    public static final int OPCODE = 10;

    private CPU cpu;

    public JmpInstruction(CPU cpu) {
        super("jmp", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {

        cpu.setIp((char) src.get(srcIndex));
        return status;
    }

    @Override
    public Status execute(int src, Status status) {

        cpu.setIp((char) src);
        return status;
    }
}
