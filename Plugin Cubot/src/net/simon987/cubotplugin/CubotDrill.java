package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.TileMap;
import org.json.simple.JSONObject;

public class CubotDrill extends CpuHardware {

    /**
     * Hardware ID (Should be unique)
     */
    public static final int HWID = 0x0005;

    public static final int DEFAULT_ADDRESS = 5;

    private static final int GATHER = 1;

    private Cubot cubot;

    public CubotDrill(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public void handleInterrupt(Status status) {
        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == GATHER) {

            int tile = cubot.getWorld().getTileMap().getTileAt(cubot.getX(), cubot.getY());

            if (tile == TileMap.IRON_TILE) {
                cubot.setHeldItem(TileMap.ITEM_IRON);

            } else if (tile == TileMap.COPPER_TILE) {
                cubot.setHeldItem(TileMap.ITEM_COPPER);

            } else {
                System.out.println("FAILED: dig");
            }

        }
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();
        json.put("hwid", HWID);
        json.put("cubot", cubot.getObjectId());

        return json;
    }

    public static CubotDrill deserialize(JSONObject hwJSON){
        return new CubotDrill((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int)(long)hwJSON.get("cubot")));
    }
}
