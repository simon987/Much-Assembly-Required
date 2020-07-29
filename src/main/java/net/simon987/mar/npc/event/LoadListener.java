package net.simon987.mar.npc.event;

import net.simon987.mar.npc.HackedNPC;
import net.simon987.mar.npc.Settlement;
import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import net.simon987.mar.server.event.LoadEvent;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class LoadListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return LoadEvent.class;
    }

    @Override
    public void handle(GameEvent event) {
        Document settlements = GameServer.INSTANCE.getUniverse().store.get("settlement_map");
        if (settlements == null) {
            return;
        }

        for (String world : settlements.keySet()) {
            Settlement.MAP.put(world, new Settlement((Document) settlements.get(world)));
        }

        try {
            InputStream is = new FileInputStream("defaultHackedCubotHardware.json");
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            String json = scanner.next();
            HackedNPC.DEFAULT_HACKED_NPC = Document.parse(json);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
