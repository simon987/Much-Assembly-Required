package net.simon987.mar.server.game.objects;

import net.simon987.mar.server.assembly.HardwareModule;
import net.simon987.mar.server.assembly.Status;

public interface HardwareHost {

    void attachHardware(HardwareModule hardware, int address);

    void detachHardware(int address);

    boolean hardwareInterrupt(int address, Status status);

    int hardwareQuery(int address);
}
