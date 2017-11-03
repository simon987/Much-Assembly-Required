package net.simon987.server.assembly.exception;

/**
 * Threw when the parser encounters an empty line
 */
public class EmptyLineException extends AssemblyException {
    public EmptyLineException(int line) {
        super("Encountered empty line", line);
    }
}
