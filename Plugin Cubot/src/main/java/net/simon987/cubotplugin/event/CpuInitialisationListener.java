package net.simon987.cubotplugin.event;

import net.simon987.cubotplugin.*;
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
        //LogManager.LOGGER.fine("(Plugin) Handled CPU Initialisation event (Cubot Plugin)");

        CPU cpu = (CPU) event.getSource();
        User user = ((CpuInitialisationEvent) event).getUser();

        CubotLeg legHw = new CubotLeg((Cubot) user.getControlledUnit());
        legHw.setCpu(cpu);
        CubotLaser laserHw = new CubotLaser((Cubot) user.getControlledUnit());
        laserHw.setCpu(cpu);
        CubotLidar radarHw = new CubotLidar((Cubot) user.getControlledUnit());
        radarHw.setCpu(cpu);
        CubotKeyboard keyboard = new CubotKeyboard((Cubot) user.getControlledUnit());
        keyboard.setCpu(cpu);
        CubotDrill drillHw = new CubotDrill((Cubot) user.getControlledUnit());
        drillHw.setCpu(cpu);
        CubotInventory invHw = new CubotInventory((Cubot) user.getControlledUnit());
        invHw.setCpu(cpu);
        CubotHologram emoteHw = new CubotHologram((Cubot) user.getControlledUnit());
        emoteHw.setCpu(cpu);
        CubotBattery batteryHw = new CubotBattery((Cubot) user.getControlledUnit());
        batteryHw.setCpu(cpu);
        CubotFloppyDrive floppyHw = new CubotFloppyDrive((Cubot) user.getControlledUnit());
        floppyHw.setCpu(cpu);
        CubotComPort comPortHw = new CubotComPort((Cubot) user.getControlledUnit());
        comPortHw.setCpu(cpu);

        cpu.attachHardware(legHw, CubotLeg.DEFAULT_ADDRESS);
        cpu.attachHardware(laserHw, CubotLaser.DEFAULT_ADDRESS);
        cpu.attachHardware(radarHw, CubotLidar.DEFAULT_ADDRESS);
        cpu.attachHardware(keyboard, CubotKeyboard.DEFAULT_ADDRESS);
        cpu.attachHardware(drillHw, CubotDrill.DEFAULT_ADDRESS);
        cpu.attachHardware(invHw, CubotInventory.DEFAULT_ADDRESS);
        cpu.attachHardware(invHw, CubotInventory.DEFAULT_ADDRESS);
        cpu.attachHardware(emoteHw, CubotHologram.DEFAULT_ADDRESS);
        cpu.attachHardware(batteryHw, CubotBattery.DEFAULT_ADDRESS);
        cpu.attachHardware(floppyHw, CubotFloppyDrive.DEFAULT_ADDRESS);
        cpu.attachHardware(comPortHw, CubotComPort.DEFAULT_ADDRESS);
    }
}
