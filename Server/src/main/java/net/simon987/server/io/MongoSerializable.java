package net.simon987.server.io;

import org.bson.Document;

public interface MongoSerializable {

    Document mongoSerialise();

}
