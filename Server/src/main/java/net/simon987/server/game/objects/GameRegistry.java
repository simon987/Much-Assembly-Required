package net.simon987.server.game.objects;

import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.world.Tile;
import net.simon987.server.logging.LogManager;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class GameRegistry {

    private HashMap<String, Class<? extends GameObject>> gameObjects;
    private HashMap<String, Class<? extends HardwareModule>> hardware;
    private HashMap<Integer, Class<? extends Item>> items;
    private HashMap<Integer, Class<? extends Tile>> tiles;


    public GameRegistry() {
        gameObjects = new HashMap<>();
        hardware = new HashMap<>();
        items = new HashMap<>();
        tiles = new HashMap<>();
    }

    public void registerGameObject(Class<? extends GameObject> clazz) {
        gameObjects.put(clazz.getCanonicalName(), clazz);
    }

    public void registerHardware(Class<? extends HardwareModule> clazz) {
        hardware.put(clazz.getCanonicalName(), clazz);
    }

    public void registerItem(int id, Class<? extends Item> clazz) {
        items.put(id, clazz);
    }

    public void registerTile(int id, Class<? extends Tile> clazz) {
        tiles.put(id, clazz);
    }

    public HardwareModule deserializeHardware(Document document, ControllableUnit controllableUnit) {
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
            LogManager.LOGGER.severe("Trying to deserialize unknown HardwareModule type: " + type);
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

    public Item deserializeItem(Document document) {

        int type = document.getInteger("type");

        if (items.containsKey(type)) {

            try {
                return items.get(type).getConstructor(Document.class).newInstance(document);
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
            LogManager.LOGGER.severe("Trying to deserialize unknown Item type: " + type);
            return null;
        }
    }

    /**
     * Creates an item with default values
     *
     * @param itemId id of the item
     */
    public Item makeItem(int itemId) {
        if (items.containsKey(itemId)) {

            try {
                return items.get(itemId).getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                    | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            LogManager.LOGGER.severe("Trying to create an unknown Item type: " + itemId);
            return null;
        }
    }

    public Tile makeTile(int tileId) {
        if (tiles.containsKey(tileId)) {

            try {
                return tiles.get(tileId).getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                    | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            LogManager.LOGGER.severe("Trying to create an unknown Tile type: " + tileId);
            return null;
        }
    }

    public boolean isGameObjectRegistered(String type) {
        return gameObjects.containsKey(type);
    }
}
