package net.simon987.server.game.objects;

import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.logging.LogManager;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class GameRegistry {

    private HashMap<String, Class<? extends GameObject>> gameObjects;
    private HashMap<String, Class<? extends CpuHardware>> hardware;


    public GameRegistry() {
        gameObjects = new HashMap<>();
        hardware = new HashMap<>();
    }

    public void registerGameObject(Class<? extends GameObject> clazz) {
        gameObjects.put(clazz.getCanonicalName(), clazz);
    }

    public void registerHardware(Class<? extends CpuHardware> clazz) {
        hardware.put(clazz.getCanonicalName(), clazz);
    }


    public CpuHardware deserializeHardware(Document document, ControllableUnit controllableUnit) {
        String type = document.getString("type");

        if (hardware.containsKey(type)) {

            try {
                return hardware.get(type).getConstructor(Document.class, ControllableUnit.class).newInstance(document, controllableUnit);
            } catch (InstantiationException | IllegalAccessException |
                    InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            LogManager.LOGGER.severe("Trying to deserialize unknown CpuHardware type: " + type);
            return null;
        }
    }

    public GameObject deserializeGameObject(Document document) {

        String type = document.getString("type");

        if (gameObjects.containsKey(type)) {

            try {
                return gameObjects.get(type).getConstructor(Document.class).newInstance(document);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            } catch (InvocationTargetException e) {
                LogManager.LOGGER.severe("Error while trying to deserialize object of type " + type + ": " + e.getTargetException().getMessage());
                LogManager.LOGGER.severe(document.toJson());
                e.getTargetException().printStackTrace();
                return null;
            }
        } else {
            LogManager.LOGGER.severe("Trying to deserialize unknown GameObject type: " + type);
            return null;
        }
    }

    public boolean isGameObjectRegistered(String type) {
        return gameObjects.containsKey(type);
    }
}
