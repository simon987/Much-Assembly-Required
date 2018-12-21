package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.objects.Structure;
import net.simon987.server.game.objects.Updatable;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.awt.*;

/**
 * Game objects that regularly creates NonPlayerCharacters
 */
public class Factory extends Structure implements Updatable {

    private static final int MAP_INFO = 0x0401;

    /**
     * Maximum number of NonPlayerCharacters assigned to this Factory
     */
    private static final int MAX_NPC_COUNT = GameServer.INSTANCE.getConfig().getInt("factory_max_npc_count");

    /**
     * Number of ticks to wait after creating a NonPlayerCharacter
     */
    private static final int NPC_CREATION_COOLDOWN = NonPlayerCharacter.LIFETIME / MAX_NPC_COUNT;

    /**
     * Number of ticks to wait until the Factory can spawn a new NPC
     */
    private int cooldown = 0;

    public Factory() {
        super(2, 2);
    }

    public Factory(Document document) {
        super(document, 2, 2);
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    /**
     * Called every tick
     * <br>The fist time this is called, NPCs retrieved from the database are linked to the Factory
     */
    @Override
    public void update() {

        Settlement settlement = NpcPlugin.settlementMap.get(getWorld().getId());

        if (cooldown == 0) {
            if (settlement.getNpcs().size() < MAX_NPC_COUNT) {
                Point p = getAdjacentTile();

                if (p != null) {
                    NonPlayerCharacter npc = new HarvesterNPC();
                    npc.setWorld(getWorld());
                    npc.setObjectId(new ObjectId());
                    npc.setX(p.x);
                    npc.setY(p.y);
                    getWorld().addObject(npc);
                    getWorld().incUpdatable();

                    settlement.addNpc(npc);
                }
            }

            cooldown += NPC_CREATION_COOLDOWN;

        } else {
            cooldown--;
        }
    }
}
