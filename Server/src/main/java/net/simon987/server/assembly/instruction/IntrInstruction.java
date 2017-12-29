package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;

public class IntrInstruction extends Instruction{
			
		public static final int OPCODE = 49;
		private CPU cpu;

		public IntrInstruction(CPU cpu) {
			super("intr", OPCODE);
			this.cpu = cpu;
		}
		
	    public Status execute(Status status) {
	    	cpu.setIp((char)cpu.getMemory().get(cpu.getRegisterSet().getRegister("SP").getValue()));
	        status.fromByte((char) cpu.getMemory().get(cpu.getRegisterSet().getRegister("SP").getValue() + 1));	        
	        cpu.getRegisterSet().getRegister("SP").setValue(cpu.getRegisterSet().getRegister("SP").getValue() + 2); //Increment SP (stack grows towards smaller)
	        return status;
	    }
		

}

