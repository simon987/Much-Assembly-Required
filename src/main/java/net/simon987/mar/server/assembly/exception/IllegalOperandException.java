package net.simon987.mar.server.assembly.exception;


public class IllegalOperandException extends AssemblyException {
    public IllegalOperandException(String msg, int line) {
        super(msg, line);
    }
}
