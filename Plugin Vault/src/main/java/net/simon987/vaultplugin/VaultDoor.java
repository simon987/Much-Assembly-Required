package net.simon987.vaultplugin;

import com.mongodb.BasicDBObject;
import net.simon987.server.game.Enterable;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.Programmable;
import net.simon987.server.game.Updatable;
import net.simon987.server.logging.LogManager;
import net.simon987.vaultplugin.RandomString;

import java.util.Arrays;


public class VaultDoor extends GameObject implements Programmable, Enterable, Updatable {

    private static final int MAP_INFO = 0x0800;

    /**
     * Password to open the vault door
     */
    private char[] password;

    private RandomString random_string_generator;

    /**
     * Whether or not the vault door is opened
     */
    private boolean open = false;


    /**
     * Number of ticks to remain the door open
     */
    private int OPEN_TIME = GameServer.INSTANCE.getConfig().getInt("vault_door_open_time");
    private int openedTimer = 0;

    private int password_length;
    private int cypher_id;

    public VaultDoor(int password_length, int cypher_id){
        this.password_length = password_length;
        this.cypher_id = cypher_id;
        this.random_string_generator = new RandomString(password_length);

        password = getRandomPassword();
    }


    @Override
    public void update() {
        if (open){
            if (openedTimer <= 0) {
                //Door was open for OPEN_TIME, close it and regen password
                password = getRandomPassword();
                open = false;
                openedTimer = 0;
                LogManager.LOGGER.fine("Closed Vault door ID: " + getObjectId());
            } 
            else{
                openedTimer--;
            }
        }

    }

    @Override
    public boolean sendMessage(char[] message) {

        if (Arrays.equals(message, password)) {
            if (!open) {
                openVault();
            } 
            else{
                keepVaultOpen();
            }
            return true;
        }
        else {
            return false;
        }
    }

    private void openVault(){
        open = true;
        openedTimer = OPEN_TIME;
        LogManager.LOGGER.fine("Opened Vault door ID: " + getObjectId());
    }

    private void keepVaultOpen(){
        open = true;
        openedTimer = OPEN_TIME;
    }  

    @Override
    public boolean enter(GameObject object) {

        LogManager.LOGGER.fine("VAULT enter " + open);

        if (open) {
            //TODO: Enter in the vault
            return true;
        } else {
            return false;
        }
    }

    /**
     * Generates a random alphanumeric string using the RandomString class
     */
    private static char[] getRandomPassword() {
        return random_string_generator.nextString().toCharArray();
    }



    private char[] encryptVernam(char[] plaintext){
        String charset = random_string_generator.alphanum;
        int charset_length = charset.length();
        int plaintext_length = plaintext.length;
        char[] cyphertext = new char[plaintext_length];
        for (int i = 0; i< plaintext_length; i++){
            int j = i % password_length;
            char p = plaintext[i];
            char q = password[j];
            int p_ind = charset.indexOf(p);
            if (p_ind == -1){
                throw InvalidCharsetException("Plaintext contains non-alphanumeric character: "+p);
            }
            int q_ind = charset.indexOf(q);
            if (q_ind == -1){
                throw InvalidCharsetException("Password contains non-alphanumeric character: "+q); // this should NEVER happen
            }
            int c_int = (p_ind+q_ind)%charset_length;
            char c = charset.charAt(c_int);
            cyphertext[i] = c;
        }
        return cyphertext;
    }

    private char[] decryptVernam(char[] cyphertext){
        String charset = random_string_generator.alphanum;
        int charset_length = charset.length();
        int cyphertext_length = cyphertext.length;
        char[] plaintext = new char[cyphertext_length];
        for (int i = 0; i< cyphertext_length; i++){
            int j = i % password_length;
            char c = cyphertext[i];
            char q = password[j];
            int c_ind = charset.indexOf(c);
            if (c_ind == -1){
                throw InvalidCharsetException("Cyphertext contains non-alphanumeric character: "+c);
            }
            int q_ind = charset.indexOf(q);
            if (q_ind == -1){
                throw InvalidCharsetException("Password contains non-alphanumeric character: "+q); // this should NEVER happen
            }
            int p_int = (c_ind-q_ind)%charset_length;
            char p = charset.charAt(p_int);
            plaintext[i] = p;
        }
        return plaintext;
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
