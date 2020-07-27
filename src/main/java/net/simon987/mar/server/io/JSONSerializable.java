package net.simon987.mar.server.io;

import org.json.simple.JSONObject;

public interface JSONSerializable {

    JSONObject jsonSerialise();

    JSONObject debugJsonSerialise();

}
