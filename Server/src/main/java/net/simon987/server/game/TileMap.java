package net.simon987.server.game;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.io.JSONSerialisable;
import net.simon987.server.io.MongoSerialisable;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * A 2D map of Tile objects of size width*height
 */
public class TileMap implements JSONSerialisable, MongoSerialisable {

    public static final int VOID = -1;
    public static final int PLAIN_TILE = 0;
    public static final int WALL_TILE = 1;
    public static final int IRON_TILE = 2;
    public static final int COPPER_TILE = 3;
    public static final int VAULT_FLOOR = 4;
    public static final int VAULT_WALL = 5;

    public static final int ITEM_IRON = 3;
    public static final int ITEM_COPPER = 4;

    /**
     * The map of tile
     */
    private int[][] tiles;

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

        tiles = new int[width][height];
    }

    public TileMap(int[][] tiles, int size) {
        this.width = size;
        this.height = size;

        this.tiles = tiles;
    }

    /**
     * Change the tile at a specified position
     * Sets the modified flag
     *
     * @param tileId id of the new Tile
     * @param x      X coordinate of the tile to set
     * @param y      Y coordinate of the tile to set
     */
    public void setTileAt(int tileId, int x, int y) {

        try {
            tiles[x][y] = tileId;
        } catch (ArrayIndexOutOfBoundsException e) {
            //Shouldn't happen
            e.printStackTrace();
        }
    }

    /**
     * Get the tile at a specific position
     *
     * @param x X coordinate of the tile to get
     * @param y Y coordinate of the tile to get
     * @return the tile at the specified position, -1 if out of bounds
     */
    public int getTileAt(int x, int y) {
        try {
            return tiles[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }

    public int[][] getTiles() {
        return tiles;
    }

    public int getWidth() {

        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();

        byte[] terrain = new byte[width * width];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                terrain[x * width + y] = (byte) tiles[x][y];
            }
        }
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(stream, compressor);

            deflaterOutputStream.write(terrain);

            deflaterOutputStream.close();
            byte[] compressedBytes = stream.toByteArray();

            json.put("z", new String(Base64.getEncoder().encode(compressedBytes)));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public BasicDBObject mongoSerialise() {

        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("tiles", tiles);

        return dbObject;

    }

    public static TileMap deserialize(DBObject object, int size) {

        BasicDBList terrain = (BasicDBList) object.get("tiles");

        int[][] tiles = new int[size][size];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                tiles[x][y] = (int) ((BasicDBList) terrain.get(x)).get(y);
            }
        }

        return new TileMap(tiles, size);

    }

    public Point getRandomTile(int tile) {

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

            if (tiles[rx][ry] == tile) {
                return new Point(rx, ry);
            }
        }
    }
}
