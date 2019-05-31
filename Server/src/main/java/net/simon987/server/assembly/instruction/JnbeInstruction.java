package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

/**
 * Jump if not below or equal
 */
public class JnbeInstruction extends JaInstruction {
    public JnbeInstruction(CPU cpu) {
        super("jnbe", cpu);
    }
}
