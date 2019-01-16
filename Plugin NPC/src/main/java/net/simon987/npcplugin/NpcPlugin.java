package net.simon987.npcplugin;

import net.simon987.npcplugin.event.CpuInitialisationListener;
import net.simon987.npcplugin.event.VaultCompleteListener;
import net.simon987.npcplugin.event.VaultWorldUpdateListener;
import net.simon987.npcplugin.event.WorldCreationListener;
import net.simon987.npcplugin.world.TileVaultFloor;
import net.simon987.npcplugin.world.TileVaultWall;
import net.simon987.server.GameServer;
import net.simon987.server.IServerConfiguration;
import net.simon987.server.game.objects.GameRegistry;
import net.simon987.server.logging.LogManager;
import net.simon987.server.plugin.ServerPlugin;
import org.bson.Document;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class NpcPlugin extends ServerPlugin {

    public static Map<String, Settlement> settlementMap;

    public static Document DEFAULT_HACKED_NPC;

    @Override
    public void init(GameServer gameServer) {

        IServerConfiguration configuration = gameServer.getConfig();
        GameRegistry registry = gameServer.getRegistry();

        listeners.add(new WorldCreationListener(configuration.getInt("settlement_spawn_rate")));
        listeners.add(new CpuInitialisationListener());
        listeners.add(new VaultWorldUpdateListener(configuration));
        listeners.add(new VaultCompleteListener());

        registry.registerGameObject(HarvesterNPC.class);
        registry.registerGameObject(Factory.class);
        registry.registerGameObject(RadioTower.class);
        registry.registerGameObject(VaultDoor.class);
        registry.registerGameObject(Obstacle.class);
        registry.registerGameObject(ElectricBox.class);
        registry.registerGameObject(Portal.class);
        registry.registerGameObject(VaultExitPortal.class);
        registry.registerGameObject(HackedNPC.class);

        registry.registerHardware(RadioReceiverHardware.class);
        registry.registerHardware(NpcBattery.class);
        registry.registerHardware(NpcInventory.class);

        registry.registerTile(TileVaultFloor.ID, TileVaultFloor.class);
        registry.registerTile(TileVaultWall.ID, TileVaultWall.class);

        settlementMap = new ConcurrentHashMap<>();

        LogManager.LOGGER.fine("(NPC Plugin) Loading default HackedNPC settings from" +
                " defaultHackedCubotHardware.json");
        InputStream is = getClass().getClassLoader().getResourceAsStream("defaultHackedCubotHardware.json");
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        String json = scanner.next();
        DEFAULT_HACKED_NPC = Document.parse(json);

        LogManager.LOGGER.info("(NPC Plugin) Initialised NPC plugin");
    }

    @Override
    public Document mongoSerialise() {
        Document document = super.mongoSerialise();

        Document settlements = new Document();
        for (String world : settlementMap.keySet()) {
            settlements.put(world, settlementMap.get(world).mongoSerialise());
        }

        document.put("settlement_map", settlements);

        return document;
    }

    @Override
    public void load(Document document) {
        super.load(document);

        Document settlements = (Document) document.get("settlement_map");

        for (String world : settlements.keySet()) {
            settlementMap.put(world, new Settlement((Document) settlements.get(world)));
        }

        LogManager.LOGGER.fine(String.format("(%s) Loaded %d settlements", name, settlementMap.size()));
    }
}
