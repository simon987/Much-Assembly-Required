package net.simon987.mar.server.event;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.game.objects.ControllableUnit;

public class CpuInitialisationEvent extends GameEvent {

    private final ControllableUnit unit;

    public CpuInitialisationEvent(CPU cpu, ControllableUnit unit) {
        setSource(cpu);
        this.unit = unit;
    }

    public ControllableUnit getUnit() {
        return unit;
    }
}
