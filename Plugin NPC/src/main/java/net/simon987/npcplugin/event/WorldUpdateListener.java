package net.simon987.npcplugin.event;

import net.simon987.npcplugin.HarvesterNPC;
import net.simon987.npcplugin.NonPlayerCharacter;
import net.simon987.server.GameServer;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.event.WorldUpdateEvent;
import net.simon987.server.game.World;
import net.simon987.server.logging.LogManager;

import java.awt.*;

public class WorldUpdateListener implements GameEventListener {

    private boolean ok = true;

    @Override
    public Class getListenedEventType() {
        return WorldUpdateEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        //LogManager.LOGGER.info("Time is " + );

        World world = ((WorldUpdateEvent) event).getWorld();

        if (GameServer.INSTANCE.getGameUniverse().getTime() % 10 == 0) {

            if (ok) {
                ok = false;
                LogManager.LOGGER.info("Spawning Harvester\n--------------------------------------");

                NonPlayerCharacter npc = new HarvesterNPC();

                Point p = world.getRandomPassableTile();

                if (p != null) {
                    npc.setWorld(world);
                    npc.setObjectId(GameServer.INSTANCE.getGameUniverse().getNextObjectId());
                    npc.setX(p.x);
                    npc.setY(p.y);
                    world.getGameObjects().add(npc);
                }


            }


        }


    }

}
