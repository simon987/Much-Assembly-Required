package net.simon987.npcplugin;


import net.simon987.server.assembly.Util;
import net.simon987.server.game.Direction;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.InventoryHolder;
import net.simon987.server.logging.LogManager;

import java.util.ArrayList;
import java.util.Random;

public class HarvestTask extends NPCTask {

    private Random random;

    private int pause;

    public HarvestTask() {
        random = new Random();
        pause = 0;
    }

    private Direction nextWorldDirection = null;

    @Override
    public boolean checkCompleted() {
        return false;
    }

    @Override
    public void tick(NonPlayerCharacter npc) {

        if (pause == 0) {
            //Get biomass
            /* todo replace by some sort of .collect call with object
            id (See https://github.com/simon987/Much-Assembly-Required/pull/66)*/
            ArrayList<GameObject> biomass = npc.getWorld().findObjects(0x4000);

            //Get closest one
            int minDist = Integer.MAX_VALUE;
            GameObject minBiomass = null;

            for (GameObject plant : biomass) {

                int dist = Util.manhattanDist(npc.getX(), npc.getY(), plant.getX(), plant.getY());

                if (dist < minDist) {
                    minDist = dist;
                    minBiomass = plant;
                }
            }

            //Move towards it
            if (minBiomass != null && minDist == 1) {
                //Reached biomass, change direction to face it
                Direction newDirection = Direction.getFacing(npc.getX(), npc.getY(),
                        minBiomass.getX(), minBiomass.getY());

                if (newDirection != null) {
                    npc.setDirection(newDirection);

                    //Reached biomass, harvest it
                    if (minBiomass instanceof InventoryHolder) {
                        ((InventoryHolder) minBiomass).takeItem(1);
                        pause += 6;
                    }


                } else {
                    LogManager.LOGGER.severe("FIXME: tick:HarvestTask, Direction is null");
                }

                nextWorldDirection = null;
            } else if (minBiomass != null && npc.moveTo(minBiomass.getX(), minBiomass.getY(), 1)) {
                //Moving towards biomass...
                nextWorldDirection = null;
            } else {

                if (nextWorldDirection == null) {

                    while (!npc.gotoWorld(nextWorldDirection)) {
                        nextWorldDirection = Direction.getDirection(random.nextInt(4));
                    }

                    pause += 6;
                } else {
                    npc.gotoWorld(nextWorldDirection);
                }
            }


        } else {
            pause--;
        }

    }


}
