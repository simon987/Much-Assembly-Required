package net.simon987.server.io;

import org.json.simple.JSONObject;

public interface JSONSerialisable {

    JSONObject jsonSerialise();

    JSONObject debugJsonSerialise();

}
