package net.simon987.mar.npc;

import net.simon987.mar.server.GameServer;
import org.bson.Document;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NpcPlugin {

    public static Map<String, Settlement> settlementMap = new ConcurrentHashMap<>();

    public static Document DEFAULT_HACKED_NPC;

    public void init(GameServer gameServer) {

    }
}
