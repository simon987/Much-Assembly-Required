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
}
