package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

/**
 * Move the execution (Jump) to an address, and save the current IP value, the execution will return to this value
 * after the RET instruction is executed
 * <br>
 * FLAGS are not altered
 */
public class CallInstruction extends Instruction {

    public static final int OPCODE = 21;

    private CPU cpu;

    public CallInstruction(CPU cpu) {
        super("call", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {
        //Push ip
        cpu.getRegisterSet().set(7, cpu.getRegisterSet().get(7) - 1); //Decrement SP (stack grows towards smaller addresses)
        cpu.getMemory().set(cpu.getRegisterSet().get(7), cpu.getIp());

        //Jmp
        cpu.setIp((char) src.get(srcIndex));

        return status;
    }

    @Override
    public Status execute(int src, Status status) {
        //Push ip
        cpu.getRegisterSet().set(7, cpu.getRegisterSet().get(7) - 1); //Decrement SP (stack grows towards smaller addresses)
        cpu.getMemory().set(cpu.getRegisterSet().get(7), cpu.getIp());

        //Jmp
        cpu.setIp((char) src);

        return status;
    }
}