package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

/**
 * Jump if not below
 */
public class JnbInstruction extends JncInstruction {
    public JnbInstruction(CPU cpu) {
        super("jae", cpu);
    }
}
