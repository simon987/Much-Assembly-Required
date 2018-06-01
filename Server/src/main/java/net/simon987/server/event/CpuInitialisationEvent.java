package net.simon987.server.event;

import net.simon987.server.assembly.CPU;
import net.simon987.server.game.objects.ControllableUnit;

public class CpuInitialisationEvent extends GameEvent {

    private ControllableUnit unit;

    public CpuInitialisationEvent(CPU cpu, ControllableUnit unit) {
        setSource(cpu);
        this.unit = unit;
    }

    public ControllableUnit getUnit() {
        return unit;
    }
}
