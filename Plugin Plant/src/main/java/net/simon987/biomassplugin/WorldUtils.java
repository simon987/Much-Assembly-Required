package net.simon987.biomassplugin;

import net.simon987.server.game.world.TileMap;
import net.simon987.server.game.world.TilePlain;
import net.simon987.server.game.world.World;
import org.bson.types.ObjectId;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class WorldUtils {

    /**
     * Generate a list of biomass blobs for a world
     */
    public static ArrayList<BiomassBlob> generateBlobs(World world, int minCount, int maxCount, int yield) {

        Random random = new Random();
        int blobCount = random.nextInt(maxCount - minCount) + minCount;
        ArrayList<BiomassBlob> biomassBlobs = new ArrayList<>(blobCount);

        //Count number of plain tiles. If there is less plain tiles than desired amount of blobs,
        //set the desired amount of blobs to the plain tile count
        TileMap m = world.getTileMap();
        int plainCount = 0;
        for (int y = 0; y < world.getWorldSize(); y++) {
            for (int x = 0; x < world.getWorldSize(); x++) {

                if (m.getTileIdAt(x, y) == TilePlain.ID) {
                    plainCount++;
                }
            }
        }

        if (blobCount > plainCount) {
            blobCount = plainCount;
        }

        outerLoop:
        for (int i = 0; i < blobCount; i++) {

            Point p = m.getRandomTile(TilePlain.ID);
            if (p != null) {

                //Don't block worlds
                int counter = 0;
                while (p.x == 0 || p.y == 0 || p.x == world.getWorldSize() - 1 || p.y == world.getWorldSize() - 1 ||
                        world.getGameObjectsAt(p.x, p.y).size() != 0) {
                    p = m.getRandomTile(TilePlain.ID);
                    counter++;

                    if (counter > 25) {
                        continue outerLoop;
                    }
                }

                for (BiomassBlob biomassBlob : biomassBlobs) {
                    if (biomassBlob.getX() == p.x && biomassBlob.getY() == p.y) {
                        //There is already a blob here
                        continue outerLoop;
                    }
                }

                BiomassBlob biomassBlob = new BiomassBlob();
                biomassBlob.setObjectId(new ObjectId());
                biomassBlob.setBiomassCount(yield);
                biomassBlob.setX(p.x);
                biomassBlob.setY(p.y);
                biomassBlob.setWorld(world);

                biomassBlobs.add(biomassBlob);
            }
        }

//        LogManager.LOGGER.info("Generated " + biomassBlobs.size() + " biomassBlobs for World (" + world.getX() + ',' +
//                world.getY() + ')');

        return biomassBlobs;
    }
}
