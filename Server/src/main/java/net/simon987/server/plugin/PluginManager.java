package net.simon987.server.plugin;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.logging.LogManager;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PluginManager {

    private ArrayList<ServerPlugin> plugins;

    public PluginManager() {
        this.plugins = new ArrayList<>(10);
    }

    public void load(File pluginFile, ServerConfiguration config) {

        LogManager.LOGGER.info("Loading plugin file " + pluginFile.getName());

        try {
            //Get the plugin config file from the archive
            ZipFile zipFile = new ZipFile(pluginFile);

            ZipEntry configEntry = zipFile.getEntry("plugin.properties");

            if (configEntry != null) {

                InputStream stream = zipFile.getInputStream(configEntry);
                Properties pluginConfig = new Properties();
                pluginConfig.load(stream);

                //Load the plugin
                ClassLoader loader = URLClassLoader.newInstance(new URL[]{pluginFile.toURI().toURL()});
                Class<?> aClass = Class.forName(pluginConfig.getProperty("classpath"), true, loader);
                Class<? extends ServerPlugin> pluginClass = aClass.asSubclass(ServerPlugin.class);
                Constructor<? extends ServerPlugin> constructor = pluginClass.getConstructor();

                ServerPlugin plugin = constructor.newInstance();
                plugin.setName(pluginConfig.getProperty("name"));
                plugin.setVersion(pluginConfig.getProperty("version"));

                LogManager.LOGGER.info("Loaded " + plugin.name + " V" + plugin.version);

                //Add it to the list
                plugins.add(plugin);

                //Init the plugin
                plugin.init(config);

            } else {
                LogManager.LOGGER.severe("Couldn't find plugin.properties in " + pluginFile.getName());
            }
            zipFile.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ServerPlugin> getPlugins() {
        return plugins;
    }
}
