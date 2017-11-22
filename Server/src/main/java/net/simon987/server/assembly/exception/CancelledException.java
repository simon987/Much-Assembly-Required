package net.simon987.server.assembly.exception;

public class CancelledException extends Exception {
    public CancelledException() {
        super("CPU Initialisation was cancelled");
    }
}
