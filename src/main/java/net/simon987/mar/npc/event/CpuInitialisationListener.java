package net.simon987.mar.npc.event;

import net.simon987.mar.npc.RadioReceiverHardware;
import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.event.CpuInitialisationEvent;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import net.simon987.mar.server.game.objects.ControllableUnit;

public class CpuInitialisationListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return CpuInitialisationEvent.class;
    }


    @Override
    public void handle(GameEvent event) {
        CPU cpu = (CPU) event.getSource();
        ControllableUnit controllableUnit = ((CpuInitialisationEvent) event).getUnit();
        cpu.setHardwareHost(controllableUnit);

        RadioReceiverHardware radioHw = new RadioReceiverHardware(controllableUnit);
        radioHw.setCpu(cpu);

        cpu.getHardwareHost().attachHardware(radioHw, RadioReceiverHardware.DEFAULT_ADDRESS);
    }
}
