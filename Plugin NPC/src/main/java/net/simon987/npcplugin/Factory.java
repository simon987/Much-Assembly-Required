package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.objects.MessageReceiver;
import net.simon987.server.game.objects.Structure;
import net.simon987.server.game.objects.Updatable;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.awt.*;
import java.util.Arrays;

/**
 * Game objects that regularly creates NonPlayerCharacters
 */
public class Factory extends Structure implements Updatable, MessageReceiver {

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

    private boolean locked = true;

    /**
     * If non-null, the next spawned NPC will be a HackedNPC and the program will be
     * injected in its memory
     */
    private char[] program;
    private int programIndex = 0;

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
                    NonPlayerCharacter npc = spawnNPC(p);
                    settlement.addNpc(npc);
                }
            }

            cooldown += NPC_CREATION_COOLDOWN;

        } else {
            cooldown--;
        }
    }

    private NonPlayerCharacter spawnNPC(Point p) {

        NonPlayerCharacter npc;
        if (programIndex == 0) {
            npc = new HarvesterNPC();
            npc.setWorld(getWorld());
            npc.setObjectId(new ObjectId());
            npc.setX(p.x);
            npc.setY(p.y);
            getWorld().addObject(npc);
            getWorld().incUpdatable();
        } else {

            npc = new HackedNPC(program);
            npc.setWorld(getWorld());
            npc.setObjectId(new ObjectId());
            npc.setX(p.x);
            npc.setY(p.y);
            getWorld().addObject(npc);
            getWorld().incUpdatable();

            System.out.println("NEW HACKED NPC");
            this.locked = true;
        }

        return npc;
    }

    @Override
    public boolean sendMessage(char[] message) {

        String strMessage = String.valueOf(message);

        System.out.println("Received message " + strMessage);
        if (locked) {
            Settlement settlement = NpcPlugin.settlementMap.get(getWorld().getId());

            if (Arrays.equals(settlement.getPassword(), message)) {
                System.out.println("Factory unlock");
                this.locked = false;

                return true;
            }
            System.out.println("Wrong password, " + strMessage + "!=" + String.valueOf(settlement.getPassword()));
        } else if (programIndex <= 2048) { //todo config

            if (programIndex == 0) {
                program = new char[2048];
            }

            System.arraycopy(message, 0, program, programIndex, message.length);
            System.out.println("Factory append code: " + strMessage);
            System.out.println("Wrote " + message.length + " chars");
            programIndex += message.length;

            return true;
        }

        return false;
    }
}
