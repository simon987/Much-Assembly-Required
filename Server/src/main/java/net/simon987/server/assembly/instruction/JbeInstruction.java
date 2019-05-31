package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

/**
 * Jump if below or equal
 */
public class JbeInstruction extends JnaInstruction {
    public JbeInstruction(CPU cpu) {
        super("jbe", cpu);
    }
}
