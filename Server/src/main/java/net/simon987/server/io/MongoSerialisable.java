package net.simon987.server.io;

import com.mongodb.BasicDBObject;

public interface MongoSerialisable {

    BasicDBObject mongoSerialise();

}
