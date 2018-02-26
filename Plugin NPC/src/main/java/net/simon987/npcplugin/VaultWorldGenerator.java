package net.simon987.npcplugin;

import net.simon987.server.game.Direction;
import net.simon987.server.game.TileMap;
import net.simon987.server.game.World;
import net.simon987.server.logging.LogManager;

import java.awt.*;
import java.util.ArrayList;

public class VaultWorldGenerator {

    public World generateVaultWorld(int x, int y, ArrayList<Direction> openings, String dimension) {

        LogManager.LOGGER.info("Generating vault World");

        /*
         * Openings are always at the same spot (marked by '?')
         * 1. Rectangles of random size are generated
         * 2. They are connected with tunnels from bottom to top,
         *  by their roomCenter, alternating between horizontal first
         *  tunnels and vertical first
         * 3. Tiles adjacent to floor tiles are set to wall tiles
         * Example map:
         *
         *  # # # # # # # - - - # ? ? # - - - - - - - - - -
         *  # o o o o o # - - - # ? ? # - - - - - - - - - -
         *  # o o o o o # # # # # # o # - - - - - - - - - -
         *  # o o o o o o o o o o o o # - - - - - - - - - -
         *  # o o o o o # # # # # # # # # # # - - - - - - -
         *  # o o o o o # - - - - # o o o o # - - - - - - -
         *  # o o o o o # # # # # # o o o o # - - - - - - -
         *  # # o o o o o o o o o o o o o o # - - - - - - -
         *  - # o # # # # # # # # # o o o o # - - - - - - -
         *  - # o # - - - - - - - # # # # # # - - - - - - -
         *  # # o # - - - - - - - - - - - - - - - - - # # #
         *  ? ? o # # # # # # # # # # # # # # # # # # # ? ?
         *  ? ? o o o o o o o o o o o o o o o o o o o o ? ?
         *  # # # # # # # # # # # # # # # # # # o # # # # #
         *  - - - - - - - - - - - - - - - - - # o # - - - -
         *  - # # # # # # # # # - - - - - - - # o # - - - -
         *  - # o o o o o o o # - - - # # # # # o # # # # -
         *  - # o o o o o o o # # # # # o o o o o o o o # -
         *  - # o o o o o o o o o o o # o o o o o o o o # -
         *  - # o o o o o o o # # # o # o o o o o o o o # -
         *  - # o o o o o o o # - # o # # # # # # # # # # -
         *  - # o o o o o o o # # # o # - - - - - - - - - -
         *  - # # # # # # # # # # ? ? # - - - - - - - - - -
         *  - - - - - - - - - - # ? ? # - - - - - - - - - -
         */

        int worldSize = 20;
        int floorTile = 4;
        int wallTile = 5;
        int minRoomCount = 3;
        int maxRoomCount = 6;
        int minRoomWidth = 4;
        int minRoomHeight = 4;
        int maxRoomWidth = 8;
        int maxRoomHeight = 8;

        ArrayList<Point> roomCenters = new ArrayList<>();

        World world = new World(x, y, new TileMap(worldSize, worldSize), dimension);

        TileMap map = world.getTileMap();

        //Create openings
        for (Direction opening : openings) {
            switch (opening) {
                case NORTH:

                    map.setTileAt(floorTile, worldSize / 2, 0);
                    map.setTileAt(floorTile, worldSize / 2, 1);
                    map.setTileAt(floorTile, worldSize / 2 - 1, 0);
                    map.setTileAt(floorTile, worldSize / 2 - 1, 1);
                    roomCenters.add(new Point(worldSize / 2, 1));
                    break;
                case EAST:

                    map.setTileAt(floorTile, worldSize - 1, worldSize / 2);
                    map.setTileAt(floorTile, worldSize - 1, worldSize / 2 - 1);
                    map.setTileAt(floorTile, worldSize - 2, worldSize / 2);
                    map.setTileAt(floorTile, worldSize - 2, worldSize / 2 - 1);
                    roomCenters.add(new Point(worldSize - 1, worldSize / 2 - 1));
                    break;
                case SOUTH:

                    map.setTileAt(floorTile, worldSize / 2, worldSize - 1);
                    map.setTileAt(floorTile, worldSize / 2, worldSize - 2);
                    map.setTileAt(floorTile, worldSize / 2 - 1, worldSize - 1);
                    map.setTileAt(floorTile, worldSize / 2 - 1, worldSize - 2);
                    roomCenters.add(new Point(worldSize / 2, worldSize - 2));
                    break;
                case WEST:

                    map.setTileAt(floorTile, 0, worldSize / 2);
                    map.setTileAt(floorTile, 0, worldSize / 2 - 1);
                    map.setTileAt(floorTile, 1, worldSize / 2);
                    map.setTileAt(floorTile, 1, worldSize / 2 - 1);
                    roomCenters.add(new Point(0, worldSize / 2 - 1));
                    break;
            }
        }

        //Generate rectangles of random size

        return world;

    }

}
