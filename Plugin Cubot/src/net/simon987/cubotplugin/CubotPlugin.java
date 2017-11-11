package net.simon987.cubotplugin;

import net.simon987.cubotplugin.event.CpuInitialisationListener;
import net.simon987.cubotplugin.event.UserCreationListener;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.game.GameObject;
import net.simon987.server.io.CpuHardwareDeserializer;
import net.simon987.server.io.GameObjectDeserializer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;
import org.json.simple.JSONObject;

public class CubotPlugin extends ServerPlugin implements GameObjectDeserializer, CpuHardwareDeserializer{


    @Override
    public void init() {
        listeners.add(new CpuInitialisationListener());
        listeners.add(new UserCreationListener());

        LogManager.LOGGER.info("Initialised Cubot plugin");
    }

    @Override
    public GameObject deserializeObject(JSONObject object) {

        int objType = (int)(long)object.get("type");

        if(objType == Cubot.ID) {

            return Cubot.deserialize(object);
        }

        return null;
    }

    @Override
    public CpuHardware deserializeHardware(JSONObject hwJson) {
        int hwid = (int)(long)hwJson.get("hwid");

        switch (hwid){
            case CubotLeg.HWID:
                return CubotLeg.deserialize(hwJson);
            case CubotLaser.HWID:
                return CubotLaser.deserialize(hwJson);
            case CubotLidar.HWID:
                return CubotLidar.deserialize(hwJson);
            case CubotDrill.HWID:
                return CubotDrill.deserialize(hwJson);
            case CubotInventory.HWID:
                return CubotInventory.deserialize(hwJson);
            case Keyboard.HWID:
                return Keyboard.deserialize(hwJson);
        }

        return null;
    }
}
