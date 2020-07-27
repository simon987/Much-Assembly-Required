package net.simon987.mar.server.io;

import org.bson.Document;

public interface MongoSerializable {

    Document mongoSerialise();

}
