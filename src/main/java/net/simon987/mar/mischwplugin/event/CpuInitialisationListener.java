package net.simon987.mar.mischwplugin.event;

import net.simon987.mar.mischwplugin.Clock;
import net.simon987.mar.mischwplugin.RandomNumberGenerator;
import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.event.CpuInitialisationEvent;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import net.simon987.mar.server.game.objects.HardwareHost;

public class CpuInitialisationListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return CpuInitialisationEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        CPU cpu = (CPU) event.getSource();
        HardwareHost cubot = ((CpuInitialisationEvent) event).getUnit();
        cpu.setHardwareHost(cubot);

        RandomNumberGenerator rngHW = new RandomNumberGenerator();
        rngHW.setCpu(cpu);
        Clock clock = new Clock();
        clock.setCpu(cpu);

        cpu.getHardwareHost().attachHardware(rngHW, RandomNumberGenerator.DEFAULT_ADDRESS);
        cpu.getHardwareHost().attachHardware(clock, Clock.DEFAULT_ADDRESS);
    }
}
