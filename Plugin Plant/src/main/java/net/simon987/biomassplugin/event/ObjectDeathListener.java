package net.simon987.biomassplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.event.ObjectDeathEvent;
import net.simon987.npcplugin.HarvesterNPC;

public class ObjectDeathListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return ObjectDeathEvent.getClass();
    }

    @Override
    public void handle(GameEvent event) {
        // a HarvesterNPC ObjectDeathEvent is received
        if (((ObjectDeathEvent)event).getSourceObjectId().equals(HarvesterNPC.ID)) {
            HarvesterNPC dyingHarvesterNPC = (HarvesterNPC)event.getSource();
            dyingHarvesterNPC.getWorld().getGameObjects.add(dyingHarvesterNPC.createBiomassBlobInPlace());
        }
    }
}
