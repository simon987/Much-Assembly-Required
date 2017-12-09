package net.simon987.server.event;

import net.simon987.server.plugin.PluginManager;
import net.simon987.server.plugin.ServerPlugin;

import java.util.ArrayList;


public class GameEventDispatcher {

    private PluginManager pluginManager;

    private ArrayList<GameEventListener> listeners;

    public GameEventDispatcher(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        listeners = new ArrayList<>(5);
    }

    public void dispatch(GameEvent event) {

        //Dispatch to 'local' listeners
        for (GameEventListener listener : listeners) {
            if (event.getClass().equals(listener.getListenedEventType())) {
                listener.handle(event);
            }
        }

        //Dispatch to plugins
        for (ServerPlugin plugin : pluginManager.getPlugins()) {
            for (GameEventListener listener : plugin.getListeners()) {
                if (event.getClass().equals(listener.getListenedEventType())) {
                    listener.handle(event);
                }
            }
        }

    }

    public ArrayList<GameEventListener> getListeners() {
        return listeners;
    }
}
