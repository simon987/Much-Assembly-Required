package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
/**
 *  Software Interrupt
 *  Pushes the flags register and the IP to the stack then
 *   Sets the IP to the CPU codeSegmentOffset + 2*src. 
 *  (x2 is to align with jmp instructions)
 *  
 *  No reserved interrupt vectors yet
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
        cpu.interrupt(false, src);
        return status;
    }
}
