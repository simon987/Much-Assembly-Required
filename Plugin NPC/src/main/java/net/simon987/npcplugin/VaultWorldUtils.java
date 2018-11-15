package net.simon987.npcplugin;

import net.simon987.npcplugin.world.TileVaultFloor;
import net.simon987.server.game.world.TileMap;
import net.simon987.server.game.world.World;
import org.bson.types.ObjectId;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class VaultWorldUtils {


    public static ArrayList<ElectricBox> generateElectricBoxes(World world, int minCount, int maxCount) {

        Random random = new Random();
        int boxesCount = random.nextInt(maxCount - minCount) + minCount;
        ArrayList<ElectricBox> electricBoxes = new ArrayList<>(boxesCount);

        //Count number of floor tiles. If there is less plain tiles than desired amount of boxes,
        //set the desired amount of blobs to the plain tile count
        TileMap m = world.getTileMap();
        int floorCount = 0;
        for (int y = 0; y < world.getWorldSize(); y++) {
            for (int x = 0; x < world.getWorldSize(); x++) {

                if (m.getTileIdAt(x, y) == TileVaultFloor.ID) {
                    floorCount++;
                }
            }
        }

        if (boxesCount > floorCount) {
            boxesCount = floorCount;
        }

        outerLoop:
        for (int i = 0; i < boxesCount; i++) {

            Point p = m.getRandomTile(TileVaultFloor.ID);
            if (p != null) {

                //Don't block worlds
                int counter = 0;
                while (p.x == 0 || p.y == 0 || p.x == world.getWorldSize() - 1 || p.y == world.getWorldSize() - 1 ||
                        world.getGameObjectsAt(p.x, p.y).size() != 0) {
                    p = m.getRandomTile(TileVaultFloor.ID);
                    counter++;

                    if (counter > 25) {
                        continue outerLoop;
                    }
                }

                for (ElectricBox box : electricBoxes) {
                    if (box.getX() == p.x && box.getY() == p.y) {
                        //There is already a box here
                        continue outerLoop;
                    }
                }

                ElectricBox box = new ElectricBox();
                box.setObjectId(new ObjectId());
                box.setX(p.x);
                box.setY(p.y);
                box.setWorld(world);

                electricBoxes.add(box);
            }
        }

        return electricBoxes;

    }
}
