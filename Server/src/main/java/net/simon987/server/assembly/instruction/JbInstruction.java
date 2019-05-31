package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

/**
 * Jump if below
 */
public class JbInstruction extends JcInstruction {
    public JbInstruction(CPU cpu) {
        super("jb", cpu);
    }
}
