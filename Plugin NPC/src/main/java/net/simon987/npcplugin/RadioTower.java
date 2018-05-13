package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.objects.Programmable;
import net.simon987.server.game.objects.Structure;
import net.simon987.server.game.objects.Updatable;
import org.bson.Document;

import java.util.ArrayList;

public class RadioTower extends Structure implements Programmable, Updatable {

    private static final int MAP_INFO = 0x1000;

    public static final int MAX_RANGE = GameServer.INSTANCE.getConfig().getInt("radio_tower_range");

    private static final int MAX_MESSAGES = 16;

    public RadioTower() {
        super(1, 1);
    }

    public RadioTower(Document document) {
        super(document, 1, 1);
        NpcPlugin.getRadioTowers().add(this);
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    /**
     * Messages from the current tick
     */
    private ArrayList<char[]> messages = new ArrayList<>(4);

    /**
     * Messages from the last tick
     */
    private ArrayList<char[]> lastMessages = new ArrayList<>(4);

    @Override
    public void update() {
        lastMessages = new ArrayList<>(messages);
        messages.clear();
    }

    @Override
    public boolean sendMessage(char[] message) {

        if (message.length < MAX_MESSAGES) {
            messages.add(message);
            return true;
        } else {
            return false;
        }
    }

    ArrayList<char[]> getMessages() {
        return lastMessages;
    }

}
