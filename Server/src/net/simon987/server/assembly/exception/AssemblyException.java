package net.simon987.server.assembly.exception;

/**
 * Threw when a problem is encountered while parsing a line
 * of a user's code, making it impossible to translate it to
 * binary code.
 */
public class AssemblyException extends Exception {

    /**
     * Line offset in the user's code.
     */
    private int line;

    /**
     * Create a new Assembly Exception
     *
     * @param msg  Message of the exception
     * @param line Line offset in the user's code.
     */
    public AssemblyException(String msg, int line) {
        super(msg);
        this.line = line;
    }

    public int getLine() {
        return line;
    }

}
