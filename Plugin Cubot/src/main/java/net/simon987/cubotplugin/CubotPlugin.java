package net.simon987.cubotplugin;

import com.mongodb.DBObject;
import net.simon987.cubotplugin.event.CpuInitialisationListener;
import net.simon987.cubotplugin.event.UserCreationListener;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.game.GameObject;
import net.simon987.server.io.CpuHardwareDeserializer;
import net.simon987.server.io.GameObjectDeserializer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;

public class CubotPlugin extends ServerPlugin implements GameObjectDeserializer, CpuHardwareDeserializer {


    @Override
    public void init(ServerConfiguration config) {
        listeners.add(new CpuInitialisationListener());
        listeners.add(new UserCreationListener());

        LogManager.LOGGER.info("Initialised Cubot plugin");
    }

    @Override
    public GameObject deserializeObject(DBObject object) {

        int objType = (int) object.get("t");

        if (objType == Cubot.ID) {

            return Cubot.deserialize(object);
        }

        return null;
    }

    @Override
    public CpuHardware deserializeHardware(DBObject obj) {
        int hwid = (int) obj.get("hwid");

        switch (hwid) {
            case CubotLeg.HWID:
                return CubotLeg.deserialize(obj);
            case CubotLaser.HWID:
                return CubotLaser.deserialize(obj);
            case CubotLidar.HWID:
                return CubotLidar.deserialize(obj);
            case CubotDrill.HWID:
                return CubotDrill.deserialize(obj);
            case CubotInventory.HWID:
                return CubotInventory.deserialize(obj);
            case CubotKeyboard.HWID:
                return CubotKeyboard.deserialize(obj);
            case CubotHologram.HWID:
                return CubotHologram.deserialize(obj);
            case CubotBattery.HWID:
                return CubotBattery.deserialize(obj);
            case CubotFloppyDrive.HWID:
                return CubotFloppyDrive.deserialize(obj);
            case CubotComPort.HWID:
                return CubotComPort.deserialize(obj);
        }

        return null;
    }
}
