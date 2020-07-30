package net.simon987.mar.server;

import net.simon987.mar.server.assembly.AssemblyResult;
import net.simon987.mar.server.assembly.CpuState;

import java.util.List;

public class TestExecutionResult {

    public CpuState state;

    public List<FakeHardwareHost.HwiCall> hwiHistory;

    private AssemblyResult ar;

    public TestExecutionResult(CpuState state, List<FakeHardwareHost.HwiCall> hwiHistory, AssemblyResult ar) {
        this.state = state;
        this.hwiHistory = hwiHistory;
        this.ar = ar;
    }

    public int regValue(String register) {
        return state.registers.getRegister(register).getValue();
    }

    public int labelOffset(String label) {
        return ar.labels.get(label);
    }

    public int memValue(int offset) {
        return state.memory.get(offset);
    }

    // Add more utility methods here
}
