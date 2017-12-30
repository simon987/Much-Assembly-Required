package net.simon987.npcplugin.event;

import net.simon987.npcplugin.RadioReceiverHardware;
import net.simon987.server.assembly.CPU;
import net.simon987.server.event.CpuInitialisationEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.user.User;

public class CpuInitialisationListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return CpuInitialisationEvent.class;
    }


    @Override
    public void handle(GameEvent event) {
        CPU cpu = (CPU) event.getSource();
        User user = ((CpuInitialisationEvent) event).getUser();

        RadioReceiverHardware radioHw = new RadioReceiverHardware(user.getControlledUnit());
        radioHw.setCpu(cpu);

        cpu.attachHardware(radioHw, RadioReceiverHardware.DEFAULT_ADDRESS);
    }
}
