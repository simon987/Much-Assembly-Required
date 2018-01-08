package net.simon987.server.assembly.exception;

/**
 * Class of exceptions that should stop assembly immediatly 
 */
public class FatalAssemblyException extends AssemblyException {

    /**
     * Message of the exception
     */
    private static final String message = "A fatal assembly error has occurred";

    /**
     * Create a new Duplicate Section Exception
     */
    public FatalAssemblyException(String msg, int line) {
        super(msg, line);
    }
}

