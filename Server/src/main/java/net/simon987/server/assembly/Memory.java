package net.simon987.server.assembly;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.io.MongoSerialisable;
import net.simon987.server.logging.LogManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

/**
 * Represents the available memory for a CPU in the game universe
 */
public class Memory implements Target, MongoSerialisable {


    /**
     * Contents of the memory
     */
    private char[] words;

    /**
     * Create an empty Memory object
     *
     * @param size Size of the memory, in words
     */
    public Memory(int size) {
        words = new char[size];
    }

    /**
     * Get the value at an address
     *
     * @param address Address of the value
     * @return 16-bit value at the specified address
     */
    @Override
    public int get(int address) {
        address = (char) address;

        if (address >= words.length) {
            LogManager.LOGGER.info("DEBUG: Trying to get memory out of bounds " + address);
            return 0;
        }

        return words[address];
    }

    /**
     * Write x words from an array at an offset
     */
    public boolean write(int offset, char[] src, int srcOffset, int count) {

        if (offset + count > this.words.length || srcOffset >= src.length || count < 0 || offset < 0) {
            return false;
        }

        System.arraycopy(src, srcOffset, this.words, offset, count);
        return true;
    }

    /**
     * Set the value at an address
     *
     * @param address address of the value to change
     * @param value   16-bit value to set
     */
    @Override
    public void set(int address, int value) {
        address = (char) address;

        if (address >= words.length) {
            LogManager.LOGGER.info("DEBUG: Trying to set memory out of bounds: " + address);
            return;
        }

        words[address] = (char) value;
    }

    /**
     * Fill the memory with 0s
     */
    public void clear() {
        Arrays.fill(words, (char) 0);
    }

    /**
     * Get byte array of the Memory object
     */
    public byte[] getBytes() {

        byte[] bytes = new byte[words.length * 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asCharBuffer().put(words);

        return bytes;
    }

    @Override
    public BasicDBObject mongoSerialise() {

        BasicDBObject dbObject = new BasicDBObject();

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Deflater compressor = new Deflater(Deflater.BEST_SPEED, true);
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(stream, compressor);
            deflaterOutputStream.write(getBytes());
            deflaterOutputStream.close();
            byte[] compressedBytes = stream.toByteArray();

            dbObject.put("zipBytes", new String(Base64.getEncoder().encode(compressedBytes)));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dbObject;
    }

    public static Memory deserialize(DBObject obj) {

        Memory memory = new Memory(0);

        String zipBytesStr = (String) obj.get("zipBytes");

        if (zipBytesStr != null) {
            byte[] compressedBytes = Base64.getDecoder().decode((String) obj.get("zipBytes"));

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Inflater decompressor = new Inflater(true);
                InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(baos, decompressor);
                inflaterOutputStream.write(compressedBytes);
                inflaterOutputStream.close();

                memory.setBytes(baos.toByteArray());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LogManager.LOGGER.severe("Memory was manually deleted");
            memory = new Memory(GameServer.INSTANCE.getConfig().getInt("memory_size"));
        }

        return memory;
    }

    public void setBytes(byte[] bytes) {
        this.words = new char[bytes.length / 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asCharBuffer().get(this.words);
    }

    public char[] getWords() {
        return words;
    }
}
