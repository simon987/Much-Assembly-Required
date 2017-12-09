package net.simon987.npcplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.event.TickEvent;
import net.simon987.server.logging.LogManager;

public class TickListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return TickEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        LogManager.LOGGER.info("Time is " + ((TickEvent)event).getTime());


//        NonPlayerCharacter npc = new HarvesterNPC();
//        GameServer.INSTANCE.getGameUniverse().getWorld(0,0).getGameObjects().add(npc);
    }

}
