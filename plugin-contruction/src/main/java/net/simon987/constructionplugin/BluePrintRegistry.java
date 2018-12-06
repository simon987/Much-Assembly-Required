package net.simon987.constructionplugin;

import net.simon987.server.logging.LogManager;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BluePrintRegistry {

    public static final BluePrintRegistry INSTANCE = new BluePrintRegistry();

    private Map<String, Class<? extends BluePrint>> blueprints;
    private Map<String, String> digitizedBlueprints;

    private BluePrintRegistry() {
        blueprints = new HashMap<>();
        digitizedBlueprints = new HashMap<>();
    }

    public void registerBluePrint(Class<? extends BluePrint> clazz) {
        blueprints.put(clazz.getCanonicalName(), clazz);
        String bpData = new String(BluePrintUtil.bluePrintData(clazz));
        digitizedBlueprints.put(bpData, clazz.getCanonicalName());
    }

    public BluePrint deserializeBlueprint(Document document) {

        String type = document.getString("type");

        if (blueprints.containsKey(type)) {

            try {
                return blueprints.get(type).getConstructor(Document.class).newInstance(document);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            } catch (InvocationTargetException e) {
                LogManager.LOGGER.severe("(Construction Plugin) Error while trying to deserialize object of type " + type + ": " + e.getTargetException().getMessage());
                LogManager.LOGGER.severe(document.toJson());
                e.getTargetException().printStackTrace();
                return null;
            }
        } else {
            LogManager.LOGGER.severe("(Construction Plugin) Trying to deserialize unknown BluePrint type: " + type);
            return null;
        }
    }

    public BluePrint deserializeBluePrint(char[] chars) {

        String bpData = new String(chars);

        if (digitizedBlueprints.containsKey(bpData)) {
            return deserializeBlueprint(new Document("type", digitizedBlueprints.get(bpData)));
        }

        return null;
    }
}
