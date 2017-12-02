package net.simon987.server.plugin;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.io.JSONSerialisable;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public abstract class ServerPlugin implements JSONSerialisable {

    /**
     * Name of the plugin
     */
    protected String name;

    /**
     * Version of the plugin
     */
    protected String version;

    /**
     * List of event listeners
     */
    protected ArrayList<GameEventListener> listeners = new ArrayList<>(5);

    /**
     * Called when the plugin is loaded
     */
    public abstract void init(ServerConfiguration config);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<GameEventListener> getListeners() {
        return listeners;
    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();

        json.put("name", name);
        json.put("version", version);

        return json;
    }
}
