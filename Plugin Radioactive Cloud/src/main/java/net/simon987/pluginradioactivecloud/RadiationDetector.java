package net.simon987.pluginradioactivecloud;

import java.util.ArrayList;

import org.bson.Document;
import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.ControllableUnit;

public class RadiationDetector extends HardwareModule {

  // Need to change to whatever the last unique address is
  public static final int DEFAULT_ADDRESS = 0x010F;

  /**
   * Hardware ID (Should be unique) -- NEEDS TO BE CHANGED
   */
  public static final char HWID = 0x010F;

  /**
   * Radiation detected by cubot
   */
  private double currentRadiation = 0;

  /**
   * Helper class for getTiles
   */
  private class Tuple {
    public final int x;
    public final int y;

    public Tuple(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  /**
   * Finds the tiles between the two tiles located at the given coordinates. The
   * tiles located at the coordinates are not included in the list.
   * 
   * @param x0 x-coordinate of first point
   * @param y0 y-coordinate of first point
   * @param x1 x-coordinate of second point
   * @param y1 y-coordinate of second point
   * @return List of tile coordinates. An empty list indicates tiles are next to
   *         each other.
   */
  public ArrayList<Tuple> getTiles(int x0, int y0, int x1, int y1) {

    ArrayList<Tuple> ret = new ArrayList<>();
    double slope;
    if (x1 > x0)
      slope = (y1 - y0) / (double) (x1 - x0);
    else {
      slope = (y0 - y1) / (double) (x0 - x1);

      // Swap values so that x0 < x1. This preps the following code where y is
      // determined by adding a step value (1) to x0 till it reaches x1.
      int tmp = x1;
      x1 = x0;
      x0 = tmp;

      tmp = y1;
      y1 = y0;
      y0 = tmp;
    }

    // If slope is zero or undefined, return tiles directly along the
    // appropriate cardinal direction.
    if (x0 == x1) {
      int smaller = y0 < y1 ? y0 : y1;
      int larger = y0 > y1 ? y0 : y1;
      System.out.printf("%d %d", smaller, larger);
      for (int i = smaller + 1; i < larger; i++) {
        ret.add(new Tuple(x0, i));
      }
    } else if (y0 == y1) {
      int smaller = x0 < x1 ? x0 : x1;
      int larger = x0 > x1 ? x0 : x1;
      for (int i = smaller + 1; i < larger; i++) {
        ret.add(new Tuple(i, y0));
      }
    } else {
      // Find all coordinates with 0.1 step
      int lastX = x0;
      int lastY = y0;
      for (int i = x0 * 10; i < x1 * 10; i += 1) {
        if (i / 10 != lastX || (int) (slope * i / 10) != lastY) {
          // Update last values
          lastX = i / 10;
          lastY = (int) (slope * i / 10);

          // Add new values to array
          ret.add(new Tuple(lastX, lastY));
        }
      }
    }

    return ret;
  }

  /**
   * Finds the Euclidean Distance between two coordinates.
   * 
   * @param x0 x-coordinate of first point
   * @param y0 y-coordinate of first point
   * @param x1 x-coordinate of second point
   * @param y1 y-coordinate of second point
   * @return distance between two points
   */
  public double getDistanceOfCoords(int x0, int y0, int x1, int y1) {
    return Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2));
  }

  public RadiationDetector(ControllableUnit unit) {
    super(null, unit);

    // Set default values
    currentRadiation = 0;
  }

  public RadiationDetector(Document document, ControllableUnit cubot) {
    super(document, cubot);

    // Set default values
    currentRadiation = 0;
  }

  @Override
  public void handleInterrupt(Status status) {

    // Fill in
  }

  @Override
  public char getId() {
    return HWID;
  }

}
