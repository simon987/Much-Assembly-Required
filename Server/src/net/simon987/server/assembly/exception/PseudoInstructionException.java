package net.simon987.server.assembly.exception;

/**
 * Threw when the parser encounters a pseudo instruction
 * (Instruction that doesn't produce any binary output
 */
public class PseudoInstructionException extends AssemblyException {
    public PseudoInstructionException(int line) {
        super("Pseudo instruction encountered", line);
    }
}
