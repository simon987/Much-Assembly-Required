package net.simon987.npcplugin.event;

import net.simon987.npcplugin.RadioReceiverHardware;
import net.simon987.server.assembly.CPU;
import net.simon987.server.event.CpuInitialisationEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.HardwareHost;

public class CpuInitialisationListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return CpuInitialisationEvent.class;
    }


    @Override
    public void handle(GameEvent event) {
        CPU cpu = (CPU) event.getSource();
        ControllableUnit controllableUnit = ((CpuInitialisationEvent) event).getUnit();
        cpu.setHardwareHost((HardwareHost) controllableUnit);

        RadioReceiverHardware radioHw = new RadioReceiverHardware(controllableUnit);
        radioHw.setCpu(cpu);

        cpu.getHardwareHost().attachHardware(radioHw, RadioReceiverHardware.DEFAULT_ADDRESS);
    }
}
