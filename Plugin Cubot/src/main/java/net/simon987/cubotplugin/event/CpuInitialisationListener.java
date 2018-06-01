package net.simon987.cubotplugin.event;

import net.simon987.cubotplugin.*;
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
        Cubot cubot = (Cubot) ((CpuInitialisationEvent) event).getUnit();
        cpu.setHardwareHost(cubot);

        CubotLeg legHw = new CubotLeg(cubot);
        legHw.setCpu(cpu);
        CubotLaser laserHw = new CubotLaser(cubot);
        laserHw.setCpu(cpu);
        CubotLidar radarHw = new CubotLidar(cubot);
        radarHw.setCpu(cpu);
        CubotKeyboard keyboard = new CubotKeyboard(cubot);
        keyboard.setCpu(cpu);
        CubotDrill drillHw = new CubotDrill(cubot);
        drillHw.setCpu(cpu);
        CubotInventory invHw = new CubotInventory(cubot);
        invHw.setCpu(cpu);
        CubotHologram emoteHw = new CubotHologram(cubot);
        emoteHw.setCpu(cpu);
        CubotBattery batteryHw = new CubotBattery(cubot);
        batteryHw.setCpu(cpu);
        CubotFloppyDrive floppyHw = new CubotFloppyDrive(cubot);
        floppyHw.setCpu(cpu);
        CubotComPort comPortHw = new CubotComPort(cubot);
        comPortHw.setCpu(cpu);
        CubotCore coreHw = new CubotCore(cubot);
        coreHw.setCpu(cpu);
        CubotShield shieldHw = new CubotShield(cubot);
        shieldHw.setCpu(cpu);

        cubot.attachHardware(legHw, CubotLeg.DEFAULT_ADDRESS);
        cubot.attachHardware(laserHw, CubotLaser.DEFAULT_ADDRESS);
        cubot.attachHardware(radarHw, CubotLidar.DEFAULT_ADDRESS);
        cubot.attachHardware(keyboard, CubotKeyboard.DEFAULT_ADDRESS);
        cubot.attachHardware(drillHw, CubotDrill.DEFAULT_ADDRESS);
        cubot.attachHardware(invHw, CubotInventory.DEFAULT_ADDRESS);
        cubot.attachHardware(invHw, CubotInventory.DEFAULT_ADDRESS);
        cubot.attachHardware(emoteHw, CubotHologram.DEFAULT_ADDRESS);
        cubot.attachHardware(batteryHw, CubotBattery.DEFAULT_ADDRESS);
        cubot.attachHardware(floppyHw, CubotFloppyDrive.DEFAULT_ADDRESS);
        cubot.attachHardware(comPortHw, CubotComPort.DEFAULT_ADDRESS);
        cubot.attachHardware(coreHw, CubotCore.DEFAULT_ADDRESS);
        cubot.attachHardware(shieldHw, CubotShield.DEFAULT_ADDRESS);
    }
}
