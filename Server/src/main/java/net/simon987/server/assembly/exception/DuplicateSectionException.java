package net.simon987.server.assembly.exception;

/**
 * Threw when a user attempts to define the same section twice
 */
public class DuplicateSectionException extends AssemblyException {

    /**
     * Message of the exception
     */
    private static final String message = "Sections can only be defined once";

    /**
     * Create a new Duplicate Section Exception
     */
    public DuplicateSectionException(int line) {
        super(message, line);
    }
}
