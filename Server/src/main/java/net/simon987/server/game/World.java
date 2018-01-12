package net.simon987.server.game;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.WorldUpdateEvent;
import net.simon987.server.game.pathfinding.Pathfinder;
import net.simon987.server.io.MongoSerialisable;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class World implements MongoSerialisable {

    /**
     * Size of the side of a world
     */
    public static final int WORLD_SIZE = 16;

    private static final char INFO_BLOCKED = 0x8000;
    private static final char INFO_IRON = 0x0200;
    private static final char INFO_COPPER = 0x0100;

    private int x;
    private int y;

    private TileMap tileMap;

    private ConcurrentHashMap<Long, GameObject> gameObjects = new ConcurrentHashMap<>(8);

    /**
     * If this number is greater than 0, the World will be updated.
     */
    private int updatable = 0;

    public World(int x, int y, TileMap tileMap) {
        this.x = x;
        this.y = y;
        this.tileMap = tileMap;
    }

    private World() {

    }

    public TileMap getTileMap() {
        return tileMap;
    }

    /**
     * Check if a tile is blocked, either by a game object or an impassable tile type
     */
    public boolean isTileBlocked(int x, int y) {

        return getGameObjectsBlockingAt(x, y).size() > 0 || tileMap.getTileAt(x, y) == TileMap.WALL_TILE;
    }

    /**
     * Computes the world's unique id from its coordinates.
     * 
     * @param x     the x coordinate of the world
     * @param y     the y coordinate of the world
     *
     * @return long
     */
    public static String idFromCoordinates(int x, int y){
        return "w-"+"0x"+Integer.toHexString(x)+"-"+"0x"+Integer.toHexString(y);
        //return ((long)x)*(((long)maxWidth)+1)+((long)y);
    }

    /**
     * Returns the world's unique id, computed with idFromCoordinates.
     * 
     * @return long
     */
    public String getId(){
        return World.idFromCoordinates(x,y);
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


    public ArrayList<GameObject> findObjects(int mapInfo) {

        ArrayList<GameObject> matchingObjects = new ArrayList<>(2);

        for (GameObject obj : gameObjects.values()) {
            if ((obj.getMapInfo() & mapInfo) == mapInfo) {
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

    public GameObject findObject(long objectId) {
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
                object.onDeadCallback();
                removeObject(object);
                //LogManager.LOGGER.fine("Removed object " + object + " id: " + object.getObjectId());
            } else if (object instanceof Updatable) {
                ((Updatable) object).update();
            }
        }
    }

    @Override
    public BasicDBObject mongoSerialise() {

        BasicDBObject dbObject = new BasicDBObject();

        BasicDBList objects = new BasicDBList();
        for (GameObject obj : gameObjects.values()) {
            objects.add(obj.mongoSerialise());
        }


        dbObject.put("_id", getId());

        dbObject.put("objects", objects);
        dbObject.put("terrain", tileMap.mongoSerialise());

        dbObject.put("x", x);
        dbObject.put("y", y);

        dbObject.put("updatable", updatable);
        dbObject.put("shouldUpdate",shouldUpdate());

        return dbObject;
    }

    @Override
    public String toString() {

        String str = "World (" + x + ", " + y + ")\n";
        int[][] tileMap = this.tileMap.getTiles();

        for (int x = 0; x < WORLD_SIZE; x++) {
            for (int y = 0; y < WORLD_SIZE; y++) {
                str += tileMap[x][y] + " ";
            }
            str += "\n";
        }

        return str;

    }

    public static World deserialize(JSONObject json) {
        World world = new World();
//    world.x = (int) (long) json.get("x");
//    world.y = (int) (long) json.get("y");
//    world.updatable = (int) (long) json.get("u");
//
//    world.tileMap = TileMap.deserialize((JSONObject) json.get("t"));
//
//
//    for (JSONObject objJson : (ArrayList<JSONObject>) json.get("o")) {
//
//        GameObject object = GameObject.deserialize(objJson);
//
//        object.setWorld(world);
//        world.gameObjects.add(object);
//    }

        return world;
    }

    public static World deserialize(DBObject dbObject) {

        World world = new World();
        world.x = (int) dbObject.get("x");
        world.y = (int) dbObject.get("y");
        world.updatable = (int) dbObject.get("updatable");

        world.tileMap = TileMap.deserialize((BasicDBObject) dbObject.get("terrain"));

        BasicDBList objects = (BasicDBList) dbObject.get("objects");

        for (Object obj : objects) {

            GameObject object = GameObject.deserialize((DBObject) obj);

            object.setWorld(world);
            world.addObject(object);
        }

        return world;

    }

    /**
     * Get a binary representation of the map as an array of 16-bit bit fields, one word for each
     * tile.
     * <p>
     * todo Performance cache this?
     */
    public char[][] getMapInfo() {

        char[][] mapInfo = new char[World.WORLD_SIZE][World.WORLD_SIZE];
        int[][] tiles = tileMap.getTiles();

        //Tile
        for (int y = 0; y < World.WORLD_SIZE; y++) {
            for (int x = 0; x < World.WORLD_SIZE; x++) {

                if (tiles[x][y] == TileMap.PLAIN_TILE) {

                    mapInfo[x][y] = 0;
                } else if (tiles[x][y] == TileMap.WALL_TILE) {

                    mapInfo[x][y] = INFO_BLOCKED;
                } else if (tiles[x][y] == TileMap.COPPER_TILE) {

                    mapInfo[x][y] = INFO_COPPER;
                } else if (tiles[x][y] == TileMap.IRON_TILE) {

                    mapInfo[x][y] = INFO_IRON;
                }
            }
        }

        //Objects
        for (GameObject obj : gameObjects.values()) {
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
     * by some CpuHardware in its current state
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

            int rx = random.nextInt(World.WORLD_SIZE);
            int ry = random.nextInt(World.WORLD_SIZE);

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


    private GameUniverse universe = null;

    public void setUniverse(GameUniverse universe){
        this.universe = universe;
    }

    public ArrayList<World> getNeighbouringLoadedWorlds(){
        ArrayList<World> neighbouringWorlds = new ArrayList<>();

        if (universe == null){
            return neighbouringWorlds;
        }

        for (int dx=-1; dx<=+1; dx+=2){
            World nw = universe.getLoadedWorld(x+dx,y);
            if (nw != null){
                neighbouringWorlds.add(nw);   
            }
        }
        for (int dy=-1; dy<=+1; dy+=2){
            World nw = universe.getLoadedWorld(x,y+dy);
            if (nw != null){
                neighbouringWorlds.add(nw);   
            }
        }

        return neighbouringWorlds;
    }

    public ArrayList<World> getNeighbouringExistingWorlds(){
        ArrayList<World> neighbouringWorlds = new ArrayList<>();

        if (universe == null){
            return neighbouringWorlds;
        }

        for (int dx=-1; dx<=+1; dx+=2){
            World nw = universe.getWorld(x+dx,y,false);
            if (nw != null){
                neighbouringWorlds.add(nw);   
            }
        }
        for (int dy=-1; dy<=+1; dy+=2){
            World nw = universe.getWorld(x,y+dy,false);
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
}
