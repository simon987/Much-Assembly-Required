package net.simon987.mischwplugin.event;

import net.simon987.mischwplugin.Clock;
import net.simon987.mischwplugin.RandomNumberGenerator;
import net.simon987.server.assembly.CPU;
import net.simon987.server.event.CpuInitialisationEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.HardwareHost;

public class CpuInitialisationListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return CpuInitialisationEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        CPU cpu = (CPU) event.getSource();
        HardwareHost cubot = (HardwareHost) ((CpuInitialisationEvent) event).getUnit();
        cpu.setHardwareHost(cubot);

        RandomNumberGenerator rngHW = new RandomNumberGenerator();
        rngHW.setCpu(cpu);
        Clock clock = new Clock();
        clock.setCpu(cpu);

        cpu.getHardwareHost().attachHardware(rngHW, RandomNumberGenerator.DEFAULT_ADDRESS);
        cpu.getHardwareHost().attachHardware(clock, Clock.DEFAULT_ADDRESS);
    }
}