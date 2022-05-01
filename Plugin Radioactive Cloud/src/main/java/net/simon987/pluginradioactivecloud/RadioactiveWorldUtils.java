package net.simon987.pluginradioactivecloud;

import net.simon987.server.game.world.TileMap;
import net.simon987.server.game.world.TilePlain;
import net.simon987.server.game.world.World;
import org.bson.types.ObjectId;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class RadioactiveWorldUtils {

  /**
   * Generate a list of radioactive obstacles for a world
   */
  public static ArrayList<RadioactiveObstacle> generateRadioactiveObstacles(World world, int minCount, int maxCount) {

    Random random = new Random();
    int radioactiveObjCount = random.nextInt(maxCount - minCount) + minCount;
    ArrayList<RadioactiveObstacle> radioactiveObstacles = new ArrayList<>(radioactiveObjCount);

    // Count number of plain tiles. If there is less plain tiles than desired amount
    // of radioactive objects, set the desired amount of radioactive objects to the
    // plain tile count
    TileMap m = world.getTileMap();
    int plainCount = world.getCount(TilePlain.ID);

    if (radioactiveObjCount > plainCount) {
      radioactiveObjCount = plainCount;
    }

    outerLoop: for (int i = 0; i < radioactiveObjCount; i++) {

      Point p = m.getRandomTile(TilePlain.ID);
      if (p != null) {

        // Don't block worlds
        int counter = 0;
        while (p.x == 0 || p.y == 0 || p.x == world.getWorldSize() - 1 || p.y == world.getWorldSize() - 1
            || world.getGameObjectsAt(p.x, p.y).size() != 0) {
          p = m.getRandomTile(TilePlain.ID);
          counter++;

          if (counter > 25) {
            continue outerLoop;
          }
        }

        for (RadioactiveObstacle radioactiveObstacle : radioactiveObstacles) {
          if (radioactiveObstacle.getX() == p.x && radioactiveObstacle.getY() == p.y) {
            // There is already a blob here
            continue outerLoop;
          }
        }

        RadioactiveObstacle radioactiveObstacle = new RadioactiveObstacle();
        radioactiveObstacle.setObjectId(new ObjectId());
        radioactiveObstacle.setX(p.x);
        radioactiveObstacle.setY(p.y);
        radioactiveObstacle.setWorld(world);

        radioactiveObstacles.add(radioactiveObstacle);
      }
    }

    return radioactiveObstacles;
  }
}
