package net.simon987.server.plugin;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.objects.GameRegistry;
import net.simon987.server.logging.LogManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PluginManager {

    private ArrayList<ServerPlugin> loadedPlugins;
    private ArrayList<ServerPlugin> toLoadPlugins;
    private ServerConfiguration config;
    private GameRegistry gameRegistry;

    public PluginManager(ServerConfiguration config, GameRegistry registry) {
        this.config = config;
        this.gameRegistry = registry;
        this.toLoadPlugins = new ArrayList<>(10);
        this.loadedPlugins = new ArrayList<>(10);
    }

    private ServerPlugin load(File pluginFile) {

        LogManager.LOGGER.info("Loading plugin file " + pluginFile.getName());

        ZipFile zipFile = null;
        try {
            //Get the plugin config file from the archive
            zipFile = new ZipFile(pluginFile);

            ZipEntry configEntry = zipFile.getEntry("plugin.properties");

            if (configEntry != null) {

                InputStream stream = zipFile.getInputStream(configEntry);
                Properties pluginConfig = new Properties();
                pluginConfig.load(stream);

                ClassLoader loader = URLClassLoader.newInstance(new URL[]{pluginFile.toURI().toURL()});
                Class<?> aClass = Class.forName(pluginConfig.getProperty("classpath"), true, loader);
                Class<? extends ServerPlugin> pluginClass = aClass.asSubclass(ServerPlugin.class);
                Constructor<? extends ServerPlugin> constructor = pluginClass.getConstructor();

                ServerPlugin plugin = constructor.newInstance();
                plugin.setName(pluginConfig.getProperty("name"));
                plugin.setVersion(pluginConfig.getProperty("version"));

                String dependStr = pluginConfig.getProperty("depend");
                if (dependStr != null) {
                    for (String dep : dependStr.split(",")) {
                        plugin.dependencies.add(dep.trim());
                    }
                }

                return plugin;

            } else {
                LogManager.LOGGER.severe("Couldn't find plugin.properties in " + pluginFile.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Load all plugins in plugins folder, if it doesn't exist, create it
     *
     * @return true if all the plugins could be loaded
     */
    public boolean loadInFolder(String dir) {

        File pluginDir = new File(dir);
        File[] pluginDirListing = pluginDir.listFiles();

        if (pluginDirListing == null) {
            if (!pluginDir.mkdir()) {
                LogManager.LOGGER.severe("Couldn't create plugin directory");
            }
            return false;
        }

        for (File pluginFile : pluginDirListing) {
            if (pluginFile.getName().endsWith(".jar")) {
                toLoadPlugins.add(load(pluginFile));
            }
        }

        while (toLoadPlugins.size() > 0) {

            ServerPlugin plugin = toLoadPlugins.get(0);

            if (!initWithDependencies(plugin)) {
                LogManager.LOGGER.severe("Plugin " + plugin.name + " has unmet dependencies: " +
                        Arrays.toString(plugin.dependencies.toArray()));
                return false;
            }
        }

        toLoadPlugins.clear();
        return true;
    }

    private boolean initWithDependencies(ServerPlugin plugin) {

        for (String depName : plugin.dependencies) {

            if (!isLoaded(depName)) {
                ServerPlugin dep = getPluginByName(depName, toLoadPlugins);

                if (dep != null) {
                    initWithDependencies(dep);
                } else {
                    return false;
                }
            }
        }

        initPlugin(plugin);
        return true;
    }

    private static ServerPlugin getPluginByName(String name, List<ServerPlugin> plugins) {

        for (ServerPlugin p : plugins) {
            if (p.name.equals(name)) {
                return p;
            }
        }

        return null;
    }

    private boolean isLoaded(String name) {
        return getPluginByName(name, loadedPlugins) != null;
    }

    private void initPlugin(ServerPlugin plugin) {

        toLoadPlugins.remove(plugin);
        loadedPlugins.add(plugin);
        plugin.init(config, gameRegistry);
    }

    public ArrayList<ServerPlugin> getPlugins() {
        return loadedPlugins;
    }
}
