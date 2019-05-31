package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

/**
 * Jump if not above or equal
 */
public class JnaeInstruction extends JcInstruction {
    public JnaeInstruction(CPU cpu) {
        super("jnae", cpu);
    }
}
