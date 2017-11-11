package net.simon987.mischwplugin.event;

import net.simon987.mischwplugin.RandomNumberGenerator;
import net.simon987.server.assembly.CPU;
import net.simon987.server.event.CpuInitialisationEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;

public class CpuInitialisationListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return CpuInitialisationEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        CPU cpu = (CPU) event.getSource();

        cpu.attachHardware(new RandomNumberGenerator(), RandomNumberGenerator.DEFAULT_ADDRESS);
    }
}