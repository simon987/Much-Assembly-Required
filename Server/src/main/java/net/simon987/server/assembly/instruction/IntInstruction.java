package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
/**
 *  Sets the PC to 0x0200 + Immediate operand
 *  
 * 
 *
 */
public class IntInstruction extends Instruction{
	
	public static final int OPCODE = 48;
	private CPU cpu;

	public IntInstruction(CPU cpu) {
		super("int", OPCODE);
		this.cpu = cpu;
	}
	
	@Override	
    public Status execute(int src, Status status) {
		cpu.Interrupt(0, src, false);
        return status;
    }

	
    public Status execute(Status status) {
    	cpu.Interrupt(0,0, false);
        return status;
    }
	

}
