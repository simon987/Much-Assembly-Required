package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;

/**
 * Interrupt Return
 * 
 * Pops the IP and status flag from the stack.
 */
public class IretInstruction extends Instruction{
				
    public static final int OPCODE = 49;
    private CPU cpu;

    public IretInstruction(CPU cpu) {
        super("intr", OPCODE);
        this.cpu = cpu;
    }
		
    public Status execute(Status status) {
        cpu.setIp((char)cpu.getMemory().get(cpu.getRegisterSet().getRegister("SP").getValue())); //IP (SP + 0)
        status.fromByte((char) cpu.getMemory().get(cpu.getRegisterSet().getRegister("SP").getValue() + 1)); //Status (SP + 1)      
        cpu.getRegisterSet().getRegister("SP").setValue(cpu.getRegisterSet().getRegister("SP").getValue() + 2); //Increment SP (stack grows towards smaller)
        return status;
    }
}

