package net.simon987.pluginradioactivecloud;

import java.util.ArrayList;

import javax.lang.model.type.UnionType;

import org.bson.Document;
import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.Radioactive;

public class RadiationDetector extends HardwareModule {

  // NEEDS TO BE CHANGED
  // Need to change to whatever the last unique address is
  public static final int DEFAULT_ADDRESS = 0x010F;

  /**
   * Hardware ID (Should be unique) -- NEEDS TO BE CHANGED
   */
  public static final char HWID = 0x010F;

  /**
   * Radiation Constants
   */
  private static final int ALPHA_BLOCKED_VALUE = 5;
  private static final int BETA_BLOCKED_VALUE = 2;
  private static final int GAMMA_BLOCKED_VALUE = 1;

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
    if (x1 > x0) {
      slope = (y1 - y0) / (double) (x1 - x0);
    } else {
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
      int smaller = Math.min(y0, y1);
      int larger = Math.max(y0, y1);
      System.out.printf("%d %d", smaller, larger);
      for (int i = smaller + 1; i < larger; i++) {
        ret.add(new Tuple(x0, i));
      }
    } else if (y0 == y1) {
      int smaller = Math.min(x0, x1);
      int larger = Math.max(x0, x1);
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
  }

  public RadiationDetector(Document document, ControllableUnit cubot) {
    super(document, cubot);
  }

  @Override
  public void handleInterrupt(Status status) {

    // Find all game entities in world
    ArrayList<GameObject> entities = new ArrayList<>(unit.getWorld().getGameObjects());

    // Check for alpha particles by finding Radioactive entities
    int alphaParticles = 0;
    int betaParticles = 0;
    int gammaParticles = 0;
    for (GameObject entity : entities) {
      if (entity instanceof Radioactive) {
        // Calculate distance between object and cubot
        double pathLength = getDistanceOfCoords(unit.getX(), unit.getY(), entity.getX(), entity.getY());
        alphaParticles += ((Radioactive) entity).getAlphaCounts(pathLength);
        betaParticles += ((Radioactive) entity).getBetaCounts(pathLength);
        gammaParticles += ((Radioactive) entity).getGammaCounts(pathLength);

        // Get all tiles in between cubot and Radioactive entity
        ArrayList<Tuple> tiles = getTiles(unit.getX(), unit.getY(), entity.getX(), entity.getY());
        for (Tuple tup : tiles) {
          // If intermediary tile is blocked, reduce alphaParticles by 5
          if (unit.getWorld().isTileBlocked(tup.x, tup.y)) {
            alphaParticles -= ALPHA_BLOCKED_VALUE;
            betaParticles -= BETA_BLOCKED_VALUE;
            gammaParticles -= GAMMA_BLOCKED_VALUE;
          }
        }
      }
    }

    // Save Alpha Radioactive Particles to register A
    getCpu().getRegisterSet().getRegister("A").setValue(alphaParticles);

    // Save Beta Radioactive Particles to register B
    getCpu().getRegisterSet().getRegister("B").setValue(betaParticles);

    // Save Gamma Radioactive Particles to register C
    getCpu().getRegisterSet().getRegister("C").setValue(gammaParticles);
  }

  @Override
  public char getId() {
    return HWID;
  }

}
