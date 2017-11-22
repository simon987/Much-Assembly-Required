package net.simon987.server.event;

import net.simon987.server.plugin.PluginManager;
import net.simon987.server.plugin.ServerPlugin;


public class GameEventDispatcher {

    private PluginManager pluginManager;

    public GameEventDispatcher(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void dispatch(GameEvent event) {
        for (ServerPlugin plugin : pluginManager.getPlugins()) {
            for (GameEventListener listener : plugin.getListeners()) {
                if (event.getClass().equals(listener.getListenedEventType())) {
                    listener.handle(event);
                }
            }
        }

    }

}
