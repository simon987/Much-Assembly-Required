package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

/**
 * Jump if above or equal
 */
public class JaeInstruction extends JncInstruction {
    public JaeInstruction(CPU cpu) {
        super("jae", cpu);
    }
}
