package net.simon987.server.assembly;

import net.simon987.server.assembly.exception.AssemblyException;

public class IllegalOperandException extends AssemblyException {
    public IllegalOperandException(String msg, int line) {
        super(msg, line);
    }
}
