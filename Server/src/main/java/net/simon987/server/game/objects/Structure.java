package net.simon987.server.game.objects;

import org.bson.Document;

import java.awt.*;

/**
 * Game object that is stationary.
 */
public abstract class Structure extends GameObject {

    /**
     * Length of the structure in tiles for the x axis
     */
    private int width;

    /**
     * Lenght of the structure in tiles for the y axis
     */
    private int height;

    public Structure(Document document, int width, int height) {
        super(document);
        this.width = width;
        this.height = height;
    }

    public Structure(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Get the first non-blocked tile that is directly adjacent to the factory
     *
     * @return The coordinates of the first non-blocked tile found, null if none is found.
     */
    public Point getAdjacentTile() {

        //Top
        for (int x = getX() - 1; x < getX() + width; x++) {
            if (!getWorld().isTileBlocked(x, getY() - 1)) {
                return new Point(x, getY() - 1);
            }
        }
        //Right
        for (int y = getY() + width; y < getY() + height; y++) {
            if (!getWorld().isTileBlocked(getX() + width, y)) {
                return new Point(getX() + width, y);
            }
        }
        //Bottom
        for (int x = getX() - 1; x < getX() + width; x++) {
            if (!getWorld().isTileBlocked(x, getY() + height)) {
                return new Point(x, getY() + height);
            }
        }
        //Left
        for (int y = getY() - 1; y < getY() + height; y++) {
            if (!getWorld().isTileBlocked(getX() - 1, y)) {
                return new Point(getX() - 1, y);
            }
        }

        return null;
    }

    @Override
    public boolean isAt(int x, int y) {

        /*
         * Object is width x height tiles, the (x,y) coordinates of the object being
         * at top-left.
         * # .
         * . .
         */
        return x >= getX() && x < getX() + width && y >= getY() && y < getY() + height;
    }

}
