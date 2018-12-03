package net.simon987.server.assembly.exception;

/**
 * Threw when offset for stored instruction/data overflows the size of memory
 */
public class OffsetOverflowException extends FatalAssemblyException {

    /**
     * Message of the exception
     */
    private static final String message = "Program data exceeds memory size ";

    /**
     * Create a new Offset Overflow Exception
     */
    public OffsetOverflowException(int offset, int memSize, int line) {
        super(message + offset + " > " + memSize, line);
    }
}
