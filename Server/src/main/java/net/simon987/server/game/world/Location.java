package net.simon987.server.game.world;

/**
 * Represents a location in the game universe
 */
public class Location {

    public int worldX;
    public int worldY;

    public String dimension;

    public int x;
    public int y;

    public Location(int worldX, int worldY, String dimension, int x, int y) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
    }

    public String getWorldId() {
        return World.idFromCoordinates(worldX, worldY, dimension);
    }

}
