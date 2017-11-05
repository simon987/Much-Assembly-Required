package net.simon987.server.io;

import net.simon987.server.game.GameObject;
import org.json.simple.JSONObject;

public interface GameObjectDeserializer {

    GameObject deserializeObject(JSONObject object);

}
