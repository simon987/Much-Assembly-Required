package net.simon987.constructionplugin.event;

import net.simon987.constructionplugin.ConstructionArmHardware;
import net.simon987.server.assembly.CPU;
import net.simon987.server.event.CpuInitialisationEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.ControllableUnit;

public class CpuInitialisationListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return CpuInitialisationEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        CPU cpu = (CPU) event.getSource();
        ControllableUnit unit = ((CpuInitialisationEvent) event).getUnit();

        ConstructionArmHardware constructionArmHardware = new ConstructionArmHardware(unit);
        constructionArmHardware.setCpu(cpu);

        unit.attachHardware(constructionArmHardware, ConstructionArmHardware.DEFAULT_ADDRESS);
    }
}
