package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;

/**
 * NOP (No operation instruction).
 * Does nothing
 */
public class NopInstruction extends Instruction {

    public NopInstruction() {
        super("nop", 63);
    }
}
