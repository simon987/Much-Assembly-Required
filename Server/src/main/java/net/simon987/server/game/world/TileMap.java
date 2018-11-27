package net.simon987.server.game.world;


import net.simon987.server.GameServer;
import net.simon987.server.game.objects.GameRegistry;
import net.simon987.server.io.JSONSerialisable;
import net.simon987.server.io.MongoSerializable;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * A 2D map of Tile objects of size width*height
 */
public class TileMap implements JSONSerialisable, MongoSerializable {

    /**
     * The map of tile
     */
    private Tile[][] tiles;

    /**
     * width, in tiles
     */
    private int width;

    /**
     * Height, in tiles
     */
    private int height;

    /**
     * Create a blank (All 0s) map
     */
    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;

        tiles = new Tile[width][height];
    }

    public TileMap(Tile[][] tiles, int size) {
        this.width = size;
        this.height = size;

        this.tiles = tiles;
    }

    /**
     * Change the tile at a specified position
     *
     * @param tileId id of the new Tile
     * @param x      X coordinate of the tile to set
     * @param y      Y coordinate of the tile to set
     */
    public void setTileAt(int tileId, int x, int y) {
        setTileAt(GameServer.INSTANCE.getRegistry().makeTile(tileId), x, y);
    }

    /**
     * Change the tile at a specified position
     *
     * @param tile new Tile
     * @param x    X coordinate of the tile to set
     * @param y    Y coordinate of the tile to set
     */
    public void setTileAt(Tile tile, int x, int y) {

        try {
            tiles[x][y] = tile;
        } catch (ArrayIndexOutOfBoundsException e) {
            //Shouldn't happen
            e.printStackTrace();
        }
    }

    /**
     * Get the tile id at a specific position
     *
     * @param x X coordinate of the tile to get
     * @param y Y coordinate of the tile to get
     * @return the tile id at the specified position, -1 if out of bounds
     */
    public int getTileIdAt(int x, int y) {
        try {
            return tiles[x][y].getId();
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }

    /**
     * Get the tile at a specific position
     *
     * @param x X coordinate of the tile to get
     * @param y Y coordinate of the tile to get
     * @return the tile id at the specified position, null if out of bounds
     */
    public Tile getTileAt(int x, int y) {
        try {
            return tiles[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }


    public int getWidth() {

        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public JSONObject jsonSerialise() {
        JSONObject json = new JSONObject();

        JSONArray terrain = new JSONArray();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                terrain.add(tiles[x][y].getId());
            }
        }

        json.put("terrain", terrain);

        return json;
    }

    @Override
    public JSONObject debugJsonSerialise() {
        return jsonSerialise();
    }

    @Override
    public Document mongoSerialise() {

        Document dbObject = new Document();

        //Flatten multi-dimensional array
        ArrayList<Integer> bsonTiles = new ArrayList<>();

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                bsonTiles.add(tiles[x][y].getId());
            }
        }

        dbObject.put("tiles", bsonTiles);

        return dbObject;

    }

    public static TileMap deserialize(Document object, int size) {

        ArrayList<Integer> terrain = (ArrayList<Integer>) object.get("tiles");
        GameRegistry reg = GameServer.INSTANCE.getRegistry();

        Tile[][] tiles = new Tile[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                tiles[x][y] = reg.makeTile(terrain.get(x * size + y));
            }
        }

        return new TileMap(tiles, size);
    }

    public Point getRandomTile(int tileId) {

        Random random = new Random();

        int counter = 0;
        while (true) {
            counter++;

            //Prevent infinite loop
            if (counter >= 2500) {
                return null;
            }

            int rx = random.nextInt(width);
            int ry = random.nextInt(height);

            if (tiles[rx][ry].getId() == tileId) {
                return new Point(rx, ry);
            }
        }
    }
}
