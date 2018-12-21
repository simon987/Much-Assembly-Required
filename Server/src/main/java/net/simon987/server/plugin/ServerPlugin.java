package net.simon987.server.plugin;

import net.simon987.server.GameServer;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.io.MongoSerializable;
import net.simon987.server.logging.LogManager;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public abstract class ServerPlugin implements MongoSerializable {

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
    protected List<GameEventListener> listeners = new ArrayList<>(5);

    protected List<String> dependencies = new ArrayList<>();

    /**
     * Called when the plugin is loaded
     */
    public abstract void init(GameServer gameServer);

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

    public List<GameEventListener> getListeners() {
        return listeners;
    }

    @Override
    public Document mongoSerialise() {
        Document document = new Document();

        document.put("version", version);

        return document;
    }

    public void load(Document document) {

        LogManager.LOGGER.fine(String.format("(%s) Loading from database", name));
        if (!version.equals(document.getString("version"))) {
            LogManager.LOGGER.warning(String.format("(%s) Version mismatch with database!" +
                    " This could cause problems. %s!=%s", name, version, document.getString("version")));
        }
    }
}
