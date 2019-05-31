package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

/**
 * Jump if not greater
 */
public class JngInstruction extends JleInstruction {
    public JngInstruction(CPU cpu) {
        super("jng", cpu);
    }
}
