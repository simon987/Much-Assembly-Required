package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

/**
 * Jump if not greater or equal
 */
public class JngeInstruction extends JlInstruction {
    public JngeInstruction(CPU cpu) {
        super("jnge", cpu);
    }
}
