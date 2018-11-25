package net.simon987.server.game.world;

import net.simon987.server.GameServer;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.exception.CancelledException;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.WorldGenerationEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

/**
 * Generates random Worlds
 */
public class WorldGenerator {

    /**
     * Minimum number of center points.
     */
    private int centerPointCountMin;

    /**
     * Maximum number of center points.
     */
    private int centerPointCountMax;

    /**
     * Number of plain Tiles for each wall Tile
     */
    private int wallPlainRatio;

    private int minIronCount;
    private int maxIronCount;
    private int minCopperCount;
    private int maxCopperCount;

    private static final int DEFAULT_WORLD_SIZE = 16;

    /**
     * Map of center points
     */
    private HashMap<Point, Integer> centerPointsMap;


    public WorldGenerator(ServerConfiguration config) {

        centerPointCountMin = config.getInt("wg_centerPointCountMin");
        centerPointCountMax = config.getInt("wg_centerPointCountMax");
        wallPlainRatio = config.getInt("wg_wallPlainRatio");
        minIronCount = config.getInt("wg_minIronCount");
        maxIronCount = config.getInt("wg_maxIronCount");
        minCopperCount = config.getInt("wg_minCopperCount");
        maxCopperCount = config.getInt("wg_maxCopperCount");
    }

    /**
     * Distance between 2 points rounded to int
     */
    private static int distanceBetween(int x1, int y1, int x2, int y2) {

        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

    }

    private int getClosestCenterPointTile(int x, int y) {

        int minDistance = 9999;
        int closest = -1;

        for (Point point : centerPointsMap.keySet()) {
            int distance = distanceBetween(point.x, point.y, x, y);

            if (distance < minDistance) {
                minDistance = distance;
                closest = centerPointsMap.get(point);
            }
        }


        return closest;

    }

    /**
     * Generates an empty World
     */
    private static World generateEmptyWorld(int locX, int locY) {

        return new World(locX, locY, new TileMap(DEFAULT_WORLD_SIZE, DEFAULT_WORLD_SIZE), "w-");
    }

    /**
     * Create a randomly generated World
     */
    public World generateWorld(int locX, int locY) throws CancelledException {
        Random random = new Random();

        World world = generateEmptyWorld(locX, locY);

        centerPointsMap = new HashMap<>(16);

        int centerPointCount = random.nextInt(centerPointCountMax - centerPointCountMin) + centerPointCountMin;

        //Create center points
        for (int i = centerPointCount; i >= 0; i--) {

            int tile = random.nextInt(wallPlainRatio) == 0 ? 1 : 0;
            centerPointsMap.put(new Point(random.nextInt(DEFAULT_WORLD_SIZE), random.nextInt(DEFAULT_WORLD_SIZE)), tile);
        }

        //Fill unset tiles
        for (int y = 0; y < DEFAULT_WORLD_SIZE; y++) {
            for (int x = 0; x < DEFAULT_WORLD_SIZE; x++) {
                int tile = getClosestCenterPointTile(x, y);
                /*
                 * There is 1-tile thick wall around the World, with 4-tile wide entrances
                 * each side. Each entrance is accessible (There is a 1-tick plain-terrain
                 * border inside the walls). The center part can be anything (hence the '*').
                 *
                 *  1 1 1 1 1 1 0 0 0 0 1 1 1 1 1 1
                 *  1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1
                 *  1 0 * * * * * * * * * * * * 0 1
                 *  1 0 * * * * * * * * * * * * 0 1
                 *  1 0 * * * * * * * * * * * * 0 1
                 *  1 0 * * * * * * * * * * * * 0 1
                 *  0 0 * * * * * * * * * * * * 0 0
                 *  0 0 * * * * * * * * * * * * 0 0
                 *  0 0 * * * * * * * * * * * * 0 0
                 *  0 0 * * * * * * * * * * * * 0 0
                 *  1 0 * * * * * * * * * * * * 0 1
                 *  1 0 * * * * * * * * * * * * 0 1
                 *  1 0 * * * * * * * * * * * * 0 1
                 *  1 0 * * * * * * * * * * * * 0 1
                 *  1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1
                 *  1 1 1 1 1 1 0 0 0 0 1 1 1 1 1 1
                 */

                if (x == 0 || x == DEFAULT_WORLD_SIZE - 1) {
                    //Vertical (West & East) walls
                    if (y < 6 || y > 9) {
                        tile = 1;
                    } else {
                        tile = 0;
                    }
                }
                if (y == 0 || y == DEFAULT_WORLD_SIZE - 1) {
                    // Horizontal (North & South) walls
                    if (x < 6 || x > 9) {
                        tile = 1;
                    } else {
                        tile = 0;
                    }
                }
                if (((x == 1 || x == DEFAULT_WORLD_SIZE - 2) && y > 0 && y < DEFAULT_WORLD_SIZE - 1) ||
                        ((y == 1 || y == DEFAULT_WORLD_SIZE - 2) && x > 0 && x < DEFAULT_WORLD_SIZE - 1)) {
                    //Inner border
                    tile = 0;
                }


                world.getTileMap().setTileAt(tile, x, y);
            }
        }

        //Replace plain tiles by iron and copper tiles
        int ironCount = random.nextInt(maxIronCount - minIronCount) + minIronCount;
        int copperCount = random.nextInt(maxCopperCount - minCopperCount) + minCopperCount;

        for (int i = 0; i < ironCount; i++) {

            Point p = world.getTileMap().getRandomTile(TilePlain.ID);

            if (p != null) {
                world.getTileMap().setTileAt(new TileIron(), p.x, p.y);
            }
        }
        for (int i = 0; i < copperCount; i++) {

            Point p = world.getTileMap().getRandomTile(TilePlain.ID);

            if (p != null) {
                world.getTileMap().setTileAt(new TileCopper(), p.x, p.y);
            }
        }

        GameEvent event = new WorldGenerationEvent(world);
        GameServer.INSTANCE.getEventDispatcher().dispatch(event);
        if (event.isCancelled()) {
            throw new CancelledException();
        }

        return world;
    }

}
