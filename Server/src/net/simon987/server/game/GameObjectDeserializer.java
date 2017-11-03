package net.simon987.server.game;

import org.json.simple.JSONObject;

public interface GameObjectDeserializer {

    GameObject deserializeObject(JSONObject object);

}
