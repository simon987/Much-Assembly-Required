package net.simon987.kilnplugin;

import net.simon987.server.game.GameObject;
import net.simon987.server.io.GameObjectDeserializer;
import net.simon987.server.plugin.ServerPlugin;
import org.json.simple.JSONObject;

public class KilnPlugin extends ServerPlugin implements GameObjectDeserializer {



    @Override
    public void init() {

    }

    @Override
    public GameObject deserializeObject(JSONObject object) {
        return null;
    }
}
