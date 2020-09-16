package net.simon987.pluginradioactivecloud;

import java.util.HashSet;

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

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }

      if (!(o instanceof Tuple)) {
        return false;
      }

      Tuple t = (Tuple) o;

      return Integer.compare(x, t.x) == 0 && Integer.compare(y, t.y) == 0;
    }
  }

  /**
   * Find tiles between two given tiles.
   */
  private HashSet<Tuple> getTiles(int x0, int y0, int x1, int y1) {

    HashSet<Tuple> ret = new HashSet<>();
    double slope = (y1 - y0) / (double) (x1 - x0);

    return ret;
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
