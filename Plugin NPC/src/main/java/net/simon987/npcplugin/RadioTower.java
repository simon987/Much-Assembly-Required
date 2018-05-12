package net.simon987.npcplugin;

import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.Programmable;
import net.simon987.server.game.objects.Updatable;
import org.bson.Document;

import java.util.ArrayList;

public class RadioTower extends GameObject implements Programmable, Updatable {

    private static final int MAP_INFO = 0x1000;

    public static final int MAX_RANGE = 3; //todo load from config

    private static final int MAX_MESSAGES = 16;

    public RadioTower() {

    }

    public RadioTower(Document document) {
        super(document);
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
