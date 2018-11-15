package net.simon987.npcplugin;

import net.simon987.server.game.objects.Direction;
import net.simon987.server.game.world.TileMap;
import net.simon987.server.game.world.TileVoid;
import net.simon987.server.game.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class VaultWorldGenerator {

    public World generateVaultWorld(int worldX, int worldY, ArrayList<Direction> openings, String dimension) {

//        LogManager.LOGGER.info("Generating vault World");

        /*
         * Openings are always at the same spot (marked by '?')
         * 1. Rectangles of random size are generated
         * 2. Openings are generated (if another vault world is connected on this direction)
         * 3. They are connected with tunnels from bottom to top,
         *  by their roomCenter, alternating between horizontal first
         *  tunnels and vertical first
         * 4. Tiles adjacent to floor tiles are set to wall tiles
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

        Random random = new Random();

        int worldSize = 20;
        int floorTile = 4;
        int wallTile = 5;
        int minRoomCount = 5;
        int maxRoomCount = 8;
        int minRoomWidth = 4;
        int minRoomHeight = 4;
        int maxRoomWidth = 8;
        int maxRoomHeight = 8;

        ArrayList<Point> roomCenters = new ArrayList<>(maxRoomCount + 4);
        ArrayList<Rectangle> rooms = new ArrayList<>(maxRoomCount);

        World world = new World(worldX, worldY, new TileMap(worldSize, worldSize), dimension);

        TileMap map = world.getTileMap();

        //Generate rectangles of random size
        int roomCount = random.nextInt(maxRoomCount - minRoomCount) + minRoomCount;

        for (int i = 0; i < roomCount; i++) {

            int roomWidth = random.nextInt(maxRoomWidth - minRoomWidth) + minRoomWidth;
            int roomHeight = random.nextInt(maxRoomHeight - minRoomHeight) + minRoomHeight;

            int attempts = 35;
            Point roomCorner;
            Rectangle room = null;

            //Try to find a spot that doesn't intersect another room
            attemptsLoop:
            while (true) {

                if (attempts < 0) {
                    roomCorner = null;
                    break;
                }

                roomCorner = new Point(random.nextInt((worldSize - roomWidth) - 2) + 2,
                        random.nextInt((worldSize - roomHeight) - 2) + 2);

                room = new Rectangle(roomCorner.x, roomCorner.y, roomWidth, roomHeight);

                for (Rectangle otherRoom : rooms) {
                    if (otherRoom.intersects(room)) {
                        attempts--;
                        continue attemptsLoop;
                    }
                }

                break;
            }

            if (roomCorner != null) {
                for (int x = roomCorner.x; x < roomCorner.x + roomWidth; x++) {
                    for (int y = roomCorner.y; y < roomCorner.y + roomHeight; y++) {
                        map.setTileAt(floorTile, x, y);
                    }
                }

                rooms.add(room);
                roomCenters.add(new Point(roomCorner.x + roomWidth / 2, roomCorner.y + roomHeight / 2));
            } else {
                break;
            }
        }

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
                    roomCenters.add(new Point(worldSize - 2, worldSize / 2 - 1));
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
                    roomCenters.add(new Point(1, worldSize / 2 - 1));
                    break;
            }
        }

        //Connect rooms together, from bottom to top
        roomCenters.sort(new RoomCenterComparator());
        boolean xFirst = true; //Start the tunnel horizontally

        for (int i = 0; i < roomCenters.size() - 1; i++) {
            //Note to self: I wouldn't bother trying to understand what's in this for loop,
            //If this needs to be modified just trash it and start over

            if (xFirst) {
                if (roomCenters.get(i + 1).x - roomCenters.get(i).x < 0) {
                    for (int x = roomCenters.get(i).x; x > roomCenters.get(i + 1).x; x--) {
                        map.setTileAt(floorTile, x, roomCenters.get(i).y);
                    }
                } else {
                    for (int x = roomCenters.get(i).x; x < roomCenters.get(i + 1).x; x++) {
                        map.setTileAt(floorTile, x, roomCenters.get(i).y);
                    }
                }

                if (roomCenters.get(i + 1).y - roomCenters.get(i).y < 0) {
                    for (int y = roomCenters.get(i).y; y > roomCenters.get(i + 1).y; y--) {
                        map.setTileAt(floorTile, roomCenters.get(i + 1).x, y);
                    }
                } else {
                    for (int y = roomCenters.get(i).y; y < roomCenters.get(i + 1).y; y++) {
                        map.setTileAt(floorTile, roomCenters.get(i + 1).x, y);
                    }
                }
            } else {

                if (roomCenters.get(i + 1).x - roomCenters.get(i).x < 0) {
                    for (int x = roomCenters.get(i).x; x > roomCenters.get(i + 1).x; x--) {
                        map.setTileAt(floorTile, x, roomCenters.get(i + 1).y);
                    }
                } else {
                    for (int x = roomCenters.get(i).x; x < roomCenters.get(i + 1).x; x++) {
                        map.setTileAt(floorTile, x, roomCenters.get(i + 1).y);
                    }
                }

                if (roomCenters.get(i + 1).y - roomCenters.get(i).y < 0) {
                    for (int y = roomCenters.get(i).y; y > roomCenters.get(i + 1).y; y--) {
                        map.setTileAt(floorTile, roomCenters.get(i).x, y);
                    }
                } else {
                    for (int y = roomCenters.get(i).y; y < roomCenters.get(i + 1).y; y++) {
                        map.setTileAt(floorTile, roomCenters.get(i).x, y);
                    }
                }
            }

            xFirst = !xFirst;
        }

        //Tiles adjacent to floor tiles are set to wall tiles
        for (int x = 0; x < worldSize; x++) {
            for (int y = 0; y < worldSize; y++) {

                if (map.getTileIdAt(x, y) != floorTile && hasTileAdjacent(x, y, map, floorTile)) {
                    map.setTileAt(wallTile, x, y);
                }
            }
        }

        //Set other tiles to 'void'
        for (int x = 0; x < worldSize; x++) {
            for (int y = 0; y < worldSize; y++) {

                if (map.getTileIdAt(x, y) != floorTile && map.getTileIdAt(x, y) != wallTile) {
                    map.setTileAt(new TileVoid(), x, y);
                }
            }
        }

        return world;

    }

    private boolean hasTileAdjacent(int x, int y, TileMap map, int tile) {

        for (int dX = -1; dX <= 1; dX++) {
            for (int dY = -1; dY <= 1; dY++) {

                if (map.getTileIdAt(x + dX, y + dY) == tile) {
                    return true;
                }
            }
        }

        return false;
    }

    private class RoomCenterComparator implements Comparator<Point> {
        @Override
        public int compare(Point o1, Point o2) {
            return o1.y - o2.y;
        }
    }


}
