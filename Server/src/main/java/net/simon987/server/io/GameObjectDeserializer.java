package net.simon987.server.io;

import com.mongodb.DBObject;
import net.simon987.server.game.GameObject;

public interface GameObjectDeserializer {

    GameObject deserializeObject(DBObject object);

}
