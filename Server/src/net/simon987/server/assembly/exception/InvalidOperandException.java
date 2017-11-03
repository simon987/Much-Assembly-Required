package net.simon987.server.assembly.exception;

/**
 * Threw when the Assembler attempts to parse a malformed or invalid operand
 */
public class InvalidOperandException extends AssemblyException {

    /**
     * Creates a new Invalid operand Exception
     *
     * @param msg  Message
     * @param line Line offset in the user's code.Used to display an error icon
     *             in the editor.
     */
    public InvalidOperandException(String msg, int line) {
        super(msg, line);
    }
}
