package net.simon987.server.game.world;

import net.simon987.server.GameServer;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.WorldUpdateEvent;
import net.simon987.server.game.GameUniverse;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.Updatable;
import net.simon987.server.game.pathfinding.Pathfinder;
import net.simon987.server.io.MongoSerializable;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class World implements MongoSerializable {

    /**
     * Size of the side of this world
     */
    private int worldSize;

    private int x;
    private int y;

    private TileMap tileMap;

    private String dimension;

    private ConcurrentHashMap<ObjectId, GameObject> gameObjects = new ConcurrentHashMap<>(8);

    /**
     * If this number is greater than 0, the World will be updated.
     */
    private int updatable = 0;

    public World(int x, int y, TileMap tileMap, String dimension) {
        this.x = x;
        this.y = y;
        this.tileMap = tileMap;
        this.dimension = dimension;

        this.worldSize = tileMap.getWidth();
    }

    private World(int worldSize) {
        this.worldSize = worldSize;
    }

    public String getDimension() {
        return dimension;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    /**
     * Check if a tile is blocked, either by a game object or an impassable tile type
     */
    public boolean isTileBlocked(int x, int y) {
        return getGameObjectsBlockingAt(x, y).size() > 0 || tileMap.getTileAt(x, y).isBlocked();
    }

    /**
     * Computes the world's unique id from its coordinates.
     *
     * @param x     the x coordinate of the world
     * @param y     the y coordinate of the world
     *
     * @return long
     */
    public static String idFromCoordinates(int x, int y, String dimension) {
        return dimension + "0x" + Integer.toHexString(x) + "-" + "0x" + Integer.toHexString(y);
    }

    /**
     * Returns the world's unique id, computed with idFromCoordinates.
     *
     * @return long
     */
    public String getId(){
        return World.idFromCoordinates(x, y, dimension);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ArrayList<GameObject> findObjects(Class clazz) {

        ArrayList<GameObject> matchingObjects = new ArrayList<>(2);

        for (GameObject obj : gameObjects.values()) {

            if (obj.getClass().equals(clazz)) {
                matchingObjects.add(obj);
            }
        }

        return matchingObjects;
    }

    public ArrayList<GameObject> findGameObjects(String type) {

        ArrayList<GameObject> matchingObjects = new ArrayList<>(2);

        for (GameObject obj : gameObjects.values()) {
            if ((obj.getClass().getName().equals(type))) {
                matchingObjects.add(obj);
            }
        }

        return matchingObjects;
    }

    public void addObject(GameObject object) {
        gameObjects.put(object.getObjectId(), object);
    }

    public void removeObject(GameObject object) {
        gameObjects.remove(object.getObjectId());
    }

    public GameObject findObject(ObjectId objectId) {
        return gameObjects.get(objectId);
    }

    /**
     * Update this World and its GameObjects
     * <br>
     * The update is handled by plugins first
     */
    public void update() {

        //Dispatch update event
        GameEvent event = new WorldUpdateEvent(this);
        GameServer.INSTANCE.getEventDispatcher().dispatch(event); //Ignore cancellation

        for (GameObject object : gameObjects.values()) {
            //Clean up dead objects
            if (object.isDead()) {
                if (!object.onDeadCallback()) {
                    removeObject(object);
                    //LogManager.LOGGER.fine("Removed object " + object + " id: " + object.getObjectId());
                } else if (object instanceof Updatable) {
                    ((Updatable) object).update();
                }

            } else if (object instanceof Updatable) {
                ((Updatable) object).update();
            }
        }
    }

    @Override
    public Document mongoSerialise() {

        Document dbObject = new Document();

        List<Document> objects = new ArrayList<>();
        for (GameObject obj : gameObjects.values()) {
            objects.add(obj.mongoSerialise());
        }

        dbObject.put("dimension", getDimension());

        dbObject.put("objects", objects);
        dbObject.put("terrain", tileMap.mongoSerialise());

        dbObject.put("x", x);
        dbObject.put("y", y);
        dbObject.put("size", worldSize);

        dbObject.put("updatable", updatable);
        dbObject.put("shouldUpdate",shouldUpdate());

        return dbObject;
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder(String.format("World (%04X, %04X)\n", x, y));

        char[][] mapInfo = getMapInfo();

        for (int y = 0; y < worldSize; y++) {
            for (int x = 0; x < worldSize; x++) {
                str.append(String.format("%04X ", (int) mapInfo[x][y]));
            }
            str.append("\n");
        }

        return str.toString();

    }

    public static World deserialize(Document dbObject) {

        World world = new World(dbObject.getInteger("size"));
        world.x = dbObject.getInteger("x");
        world.y = dbObject.getInteger("y");
        world.dimension = dbObject.getString("dimension");
        world.updatable = dbObject.getInteger("updatable");

        world.tileMap = TileMap.deserialize((Document) dbObject.get("terrain"), world.getWorldSize());

        ArrayList objects = (ArrayList) dbObject.get("objects");

        for (Object obj : objects) {

            GameObject object = GameServer.INSTANCE.getRegistry().deserializeGameObject((Document) obj);

            object.setWorld(world);
            world.addObject(object);

            object.initialize();
        }

        return world;
    }

    /**
     * Get a binary representation of the map as an array of 16-bit bit fields, one word for each
     * tile.
     * <p>
     * Each tile is represented as such: <code>OOOOOOOOTTTTTTTB</code> where O is the object,
     * T the tile and B if the tile is blocked or not
     */
    public char[][] getMapInfo() {

        char[][] mapInfo = new char[worldSize][worldSize];

        //Tile
        for (int x = 0; x < worldSize; x++) {
            for (int y = 0; y < worldSize; y++) {
                Tile tile = tileMap.getTileAt(x, y);

                mapInfo[x][y] = (char) (tile.isBlocked() ? 1 : 0);
                mapInfo[x][y] |= (char) (tile.getId() << 1);
            }
        }

        for (GameObject obj : gameObjects.values()) {
            //Overwrite, only the last object on a tile is considered but the blocked bit is kept
            mapInfo[obj.getX()][obj.getY()] &= 0x00FE;
            mapInfo[obj.getX()][obj.getY()] |= obj.getMapInfo();
        }

        return mapInfo;
    }

    /**
     * Get a random tile that is empty and passable
     * The function ensures that a object spawned there will not be trapped
     * and will be able to leave the World
     * <br>
     * Note: This function is quite expensive and shouldn't be used
     * by some HardwareModule in its current state
     *
     * @return random non-blocked tile
     */
    public Point getRandomPassableTile() {
        Random random = new Random();

        int counter = 0;
        while (true) {
            counter++;

            //Prevent infinite loop
            if (counter >= 1000) {
                return null;
            }

            int rx = random.nextInt(worldSize);
            int ry = random.nextInt(worldSize);

            if (!isTileBlocked(rx, ry)) {

                Object path = Pathfinder.findPath(this, rx, ry, 0, 6, 0);

                if (path != null) {
                    return new Point(rx, ry);
                }
            }
        }
    }

    /**
     * Get the list of game objects that are blocking a tile at a set of coordinates
     *
     * @param x X coordinate on the World
     * @param y Y coordinate on the World
     * @return the list of game objects blocking a location
     */
    public ArrayList<GameObject> getGameObjectsBlockingAt(int x, int y) {

        ArrayList<GameObject> objectsLooking = new ArrayList<>(2);
        for (GameObject obj : gameObjects.values()) {

            if (obj.isAt(x, y)) {
                objectsLooking.add(obj);
            }

        }

        return objectsLooking;
    }

    /**
     * Get the list of game objects that are exactly at a given location
     * <br>
     * Note: Objects like the Factory that are more than 1x1 tiles wide will only be returned
     * when their exact coordinates are specified
     *
     * @param x X coordinate on the World
     * @param y Y coordinate on the World
     * @return the list of game objects at a location
     */
    public ArrayList<GameObject> getGameObjectsAt(int x, int y) {
        ArrayList<GameObject> objectsAt = new ArrayList<>(2);
        for (GameObject obj : gameObjects.values()) {

            if (obj.isAt(x, y)) {
                objectsAt.add(obj);
            }

        }
        return objectsAt;
    }

    public void incUpdatable() {
        updatable++;
    }

    public void decUpdatable() {
        updatable--;
    }

    public boolean shouldUpdate() {
        return updatable > 0;
    }

    public int getWorldSize() {
        return worldSize;
    }


    private GameUniverse universe = null;

    public void setUniverse(GameUniverse universe){
        this.universe = universe;
    }

    private ArrayList<World> getNeighbouringLoadedWorlds() {
        ArrayList<World> neighbouringWorlds = new ArrayList<>();

        if (universe == null){
            return neighbouringWorlds;
        }

        for (int dx=-1; dx<=+1; dx+=2){
            World nw = universe.getLoadedWorld(x + dx, y, dimension);
            if (nw != null){
                neighbouringWorlds.add(nw);
            }
        }
        for (int dy=-1; dy<=+1; dy+=2){
            World nw = universe.getLoadedWorld(x, y + dy, dimension);
            if (nw != null){
                neighbouringWorlds.add(nw);
            }
        }

        return neighbouringWorlds;
    }

    public boolean canUnload(){
        return updatable==0;
    }

    public boolean shouldUnload(){
        boolean res = canUnload();

        for (World nw : getNeighbouringLoadedWorlds() ){
            res &= nw.canUnload();
        }

        return res;
    }

    public Collection<GameObject> getGameObjects() {
        return gameObjects.values();
    }


    /**
     * Get a random tile with N adjacent non-blocked tile
     *
     * @param n Number of adjacent tiles of type X
     * @return null if no tile is found
     */
    public Point getRandomTileWithAdjacent(int n, int tile) {
        int counter = 0;
        while (true) {
            counter++;

            //Prevent infinite loop
            if (counter >= 2500) {
                return null;
            }

            Point rTile = getTileMap().getRandomTile(tile);

            if (rTile != null) {
                int adjacentTiles = 0;

                if (!isTileBlocked(rTile.x, rTile.y - 1)) {
                    adjacentTiles++;
                }
                if (!isTileBlocked(rTile.x + 1, rTile.y)) {
                    adjacentTiles++;
                }
                if (!isTileBlocked(rTile.x, rTile.y + 1)) {
                    adjacentTiles++;
                }
                if (!isTileBlocked(rTile.x - 1, rTile.y)) {
                    adjacentTiles++;
                }
                if (!isTileBlocked(rTile.x + 1, rTile.y + 1)) {
                    adjacentTiles++;
                }
                if (!isTileBlocked(rTile.x - 1, rTile.y + 1)) {
                    adjacentTiles++;
                }
                if (!isTileBlocked(rTile.x + 1, rTile.y - 1)) {
                    adjacentTiles++;
                }
                if (!isTileBlocked(rTile.x - 1, rTile.y - 1)) {
                    adjacentTiles++;
                }

                if (adjacentTiles >= n) {
                    return rTile;
                }
            }
        }
    }
}
