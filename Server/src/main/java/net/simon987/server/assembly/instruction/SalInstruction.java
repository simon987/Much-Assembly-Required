package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;

/**
 * Alias of SHL instruction
 */
public class SalInstruction extends Instruction {

    public SalInstruction() {
        super("sal", ShlInstruction.OPCODE);
    }
}
