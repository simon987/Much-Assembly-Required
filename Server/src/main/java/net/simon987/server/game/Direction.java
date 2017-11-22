package net.simon987.server.game;

/**
 * Direction of a game object in a 4-direction grid-based
 * area
 */
public enum Direction {
    /**
     * North, up
     */
    NORTH,
    /**
     * East, right
     */
    EAST,
    /**
     * South, bottom
     */
    SOUTH,
    /**
     * West, left
     */
    WEST;

    public static Direction getDirection(int x) {
        switch (x) {
            case 0:
                return NORTH;
            case 1:
                return EAST;
            case 2:
                return SOUTH;
            case 3:
                return WEST;
            default:
                return null;
        }
    }

    /**
     * Get the direction so that the object at (x1, y2) faces the object at (x2, y2),
     * assumes that the objects are 1 tile away (manhattan distance)
     *
     * @return the Direction of the first coordinates so that it faces the second coordinates
     */
    public static Direction getFacing(int x1, int y1, int x2, int y2) {

        if (x2 < x1) {
            return WEST;
        } else if (x2 > x1) {
            return EAST;
        } else if (y2 < y1) {
            return NORTH;
        } else if (y2 > y1) {
            return SOUTH;
        } else {
            return null;
        }

    }
}
