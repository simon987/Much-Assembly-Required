package net.simon987.vaultplugin;

import com.mongodb.BasicDBObject;
import net.simon987.server.game.Enterable;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.Programmable;
import net.simon987.server.game.Updatable;
import net.simon987.server.logging.LogManager;

import java.util.Arrays;


public class VaultDoor extends GameObject implements Programmable, Enterable, Updatable {

    private static final int MAP_INFO = 0x0800;

    /**
     * Password to open the vault door
     */
    private char[] password;

    /**
     * Whether or not the vault door is opened
     */
    private boolean opened;

    private int openedTimer;

    /**
     * Number of ticks to remain the door open
     */
    private static final int OPEN_TIME = 4; //todo load from config


    @Override
    public void update() {

        if (openedTimer <= 0) {

            //Door was opened for OPEN_TIME, close it and regen password
            password = getRandomPassword();
            opened = false;

            LogManager.LOGGER.fine("Closed Vault door ID: " + getObjectId());
        } else {
            openedTimer--;
        }

    }

    @Override
    public boolean sendMessage(char[] message) {

        System.out.println("VAULT: sendMessage" + new String(message));//todo rmv

        if (!opened) {

            if (Arrays.equals(message, password)) {
                opened = true;
                openedTimer = OPEN_TIME;

                LogManager.LOGGER.fine("Opened Vault door ID: " + getObjectId());
            }

            return true;
        } else {
            //Can't receive messages when opened
            return false;
        }
    }

    @Override
    public boolean enter(GameObject object) {

        LogManager.LOGGER.fine("VAULT enter " + opened);

        if (opened) {

            //TODO: Enter in the vault


            return true;
        } else {
            return false;
        }

    }

    private static char[] getRandomPassword() {
        return "12345678".toCharArray();//todo actual random password
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public BasicDBObject mongoSerialise() {
        return null;
    }
}
