package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

/**
 * Jump if not equal
 */
public class JneInstruction extends JnzInstruction {
    public JneInstruction(CPU cpu) {
        super("jne", cpu);
    }
}
