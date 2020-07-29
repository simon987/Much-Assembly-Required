package net.simon987.mar.npc.event;

import net.simon987.mar.npc.NpcPlugin;
import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.event.BeforeSaveEvent;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import org.bson.Document;

public class BeforeSaveListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return BeforeSaveEvent.class;
    }

    @Override
    public void handle(GameEvent event) {
        Document settlements = new Document();
        for (String world : NpcPlugin.settlementMap.keySet()) {
            settlements.put(world, NpcPlugin.settlementMap.get(world).mongoSerialise());
        }

        GameServer.INSTANCE.getUniverse().store.put("settlement_map", settlements);
    }
}
