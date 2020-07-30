package net.simon987.mar.server;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.CpuState;
import net.simon987.mar.server.assembly.HardwareModule;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.game.objects.HardwareHost;

import java.util.ArrayList;
import java.util.List;


public class FakeHardwareHost implements HardwareHost {

    public final List<HwiCall> callHistory = new ArrayList<>();
    private final CPU cpu;

    public FakeHardwareHost(CPU cpu) {
        this.cpu = cpu;
    }

    @Override
    public void attachHardware(HardwareModule hardware, int address) {
        // noop
    }

    @Override
    public void detachHardware(int address) {
        // noop
    }

    @Override
    public boolean hardwareInterrupt(int address, Status status) {
        callHistory.add(new HwiCall(address, cpu.getState()));
        return true;
    }

    @Override
    public int hardwareQuery(int address) {
        return -1;
    }

    public static class HwiCall {
        public HwiCall(int address, CpuState state) {
            this.address = address;
            this.state = state;
        }

        public int address;
        public CpuState state;
    }
}

