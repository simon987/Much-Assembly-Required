package net.simon987.cubotplugin;

import net.simon987.cubotplugin.event.*;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.objects.GameRegistry;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;

public class CubotPlugin extends ServerPlugin {


    @Override
    public void init(ServerConfiguration config, GameRegistry registry) {
        listeners.add(new CpuInitialisationListener());
        listeners.add(new UserCreationListener());
        //Debug commands
        listeners.add(new ChargeShieldCommandListener());
        listeners.add(new SetInventoryPosition());
        listeners.add(new PutItemCommandListener());
        listeners.add(new PopItemCommandListener());

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

        LogManager.LOGGER.info("(Cubot Plugin) Initialised Cubot plugin");
    }
}
