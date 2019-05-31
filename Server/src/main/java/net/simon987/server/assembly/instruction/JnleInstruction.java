package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

/**
 * Jump if not lower or equal
 */
public class JnleInstruction extends JgInstruction {
    public JnleInstruction(CPU cpu) {
        super("jnle", cpu);
    }
}
