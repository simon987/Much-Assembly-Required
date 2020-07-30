package net.simon987.mar.server.assembly;

public class CpuState {

    public RegisterSet registers;

    public Memory memory;

    public Status status;

    public CpuState(RegisterSet registers, Memory memory, Status status) {
        this.registers = registers;
        this.memory = memory;
        this.status = status;
    }
}
