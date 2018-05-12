package net.simon987.cubotplugin;

import net.simon987.cubotplugin.event.ChargeShieldCommandListener;
import net.simon987.cubotplugin.event.CpuInitialisationListener;
import net.simon987.cubotplugin.event.UserCreationListener;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.objects.GameRegistry;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;

public class CubotPlugin extends ServerPlugin {


    @Override
    public void init(ServerConfiguration config, GameRegistry registry) {
        listeners.add(new CpuInitialisationListener());
        listeners.add(new UserCreationListener());
        listeners.add(new ChargeShieldCommandListener());

        registry.registerGameObject(Cubot.class);

        registry.registerHardware(CubotLeg.class);
        registry.registerHardware(CubotLaser.class);
        registry.registerHardware(CubotLidar.class);
        registry.registerHardware(CubotDrill.class);
        registry.registerHardware(CubotInventory.class);
        registry.registerHardware(CubotKeyboard.class);
        registry.registerHardware(CubotHologram.class);
        registry.registerHardware(CubotBattery.class);
        registry.registerHardware(CubotFloppyDrive.class);
        registry.registerHardware(CubotComPort.class);
        registry.registerHardware(CubotShield.class);
        registry.registerHardware(CubotCore.class);

        LogManager.LOGGER.info("Initialised Cubot plugin");
    }
}
