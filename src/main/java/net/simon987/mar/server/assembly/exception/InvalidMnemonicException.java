package net.simon987.mar.server.assembly.exception;

/**
 * Threw when the parse encounters an invalid mnemonic
 */
public class InvalidMnemonicException extends AssemblyException {
    public InvalidMnemonicException(String mnemonic, int line) {
        super("Unknown mnemonic \"" + mnemonic + "\"", line);
    }
}
