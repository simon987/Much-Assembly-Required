package net.simon987.mar.npcplugin;

import net.simon987.mar.server.GameServer;
import org.bson.Document;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class NpcPlugin {

    public static Map<String, Settlement> settlementMap;

    public static Document DEFAULT_HACKED_NPC;

    public void init(GameServer gameServer) {
        // TODO: save this in GameUniverse.store
        settlementMap = new ConcurrentHashMap<>();

        // TODO: load from file relpath
        InputStream is = getClass().getClassLoader().getResourceAsStream("defaultHackedCubotHardware.json");
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        String json = scanner.next();
        DEFAULT_HACKED_NPC = Document.parse(json);
    }
}
