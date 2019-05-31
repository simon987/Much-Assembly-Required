package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

/**
 * Jump if not lower
 */
public class JnlInstruction extends JgeInstruction {
    public JnlInstruction(CPU cpu) {
        super("jnl", cpu);
    }
}
