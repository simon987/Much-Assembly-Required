package net.simon987.server.io;

import net.simon987.server.game.GameObject;
import org.bson.Document;

public interface GameObjectDeserializer {

    GameObject deserializeObject(Document object);

}
