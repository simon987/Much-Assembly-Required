package net.simon987.server.assembly.exception;

/**
 * Threw when a user attempts to define the same section twice
 */
public class DuplicateSegmentException extends AssemblyException {

    /**
     * Message of the exception
     */
    private static final String message = "Segments can only be defined once";

    /**
     * Create a new Duplicate Segment Exception
     */
    public DuplicateSegmentException(int line) {
        super(message, line);
    }
}
