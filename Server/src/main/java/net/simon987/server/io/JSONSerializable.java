package net.simon987.server.io;

import org.json.simple.JSONObject;

public interface JSONSerializable {

    JSONObject jsonSerialise();

    JSONObject debugJsonSerialise();

}
