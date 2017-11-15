package net.simon987.server.assembly;


import net.simon987.server.io.JSONSerialisable;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

/**
 * Represents the available memory for a CPU in the game universe
 */
public class Memory implements Target, JSONSerialisable {


    /**
     * Contents of the memory
     */
    private byte[] bytes;

    /**
     * Create an empty Memory object
     *
     * @param size Size of the memory, in words
     */
    public Memory(int size) {
        bytes = new byte[size];
    }

    /**
     * Get the value at an address
     *
     * @param address Address of the value
     * @return 16-bit value at the specified address
     */
    @Override
    public int get(int address) {
        address = address * 2; //Because our Memory is only divisible by 16bits

        if (address + 2 > bytes.length) {
            LogManager.LOGGER.info("DEBUG: Trying to get memory out of bounds " + address);
            return 0;
        }

        return ((bytes[address] & 0xFF) << 8) | (bytes[address + 1] & 0xFF);
    }

    /**
     * Write x words from an array at an offset
     */
    public boolean write(int offset, byte[] bytes, int srcOffset, int count) {

        offset = (char)offset * 2;


        if (offset + count > this.bytes.length || srcOffset >= bytes.length || count < 0 || offset < 0) {
            return false;
        }

        System.arraycopy(bytes, srcOffset, this.bytes, offset, count);
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

        address = (char)address * 2;


        if (address + 2 > bytes.length) {
            LogManager.LOGGER.info("DEBUG: Trying to set memory out of bounds: " + address);
            return;
        }

        bytes[address] = (byte) ((value >> 8));
        bytes[address + 1] = (byte) (value & 0xFF);
    }

    /**
     * Fill the memory with 0s
     */
    public void clear() {
        Arrays.fill(bytes, (byte) 0);
    }

    /**
     * Get byte array of the Memory object
     */
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(stream, compressor);
            deflaterOutputStream.write(bytes);
            deflaterOutputStream.close();
            byte[] compressedBytes = stream.toByteArray();

            json.put("zipBytes", new String(Base64.getEncoder().encode(compressedBytes)));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    public static Memory deserialize(JSONObject json){

        Memory memory = new Memory(0);
        byte[] compressedBytes = Base64.getDecoder().decode((String)json.get("zipBytes"));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Inflater decompressor = new Inflater(true);
            InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(baos, decompressor);
            inflaterOutputStream.write(compressedBytes);
            inflaterOutputStream.close();

            memory.bytes = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return memory;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
