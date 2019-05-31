package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;

/**
 * Jump if equal
 */
public class JeInstruction extends JzInstruction {
    public JeInstruction(CPU cpu) {
        super("je", cpu);
    }
}
