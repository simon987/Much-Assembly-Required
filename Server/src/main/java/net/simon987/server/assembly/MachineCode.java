package net.simon987.server.assembly;

import net.simon987.server.logging.LogManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


/**
 * Represents an encoded instruction. this class is used to easily
 * write to an 16bit value.
 */
public class MachineCode {

    /**
     * Value of the initial 2-byte instruction
     */
    private char value;

    /**
     * Appended words after the instruction bytes. Used to store immediate values
     */
    private ArrayList<Character> additionalWords = new ArrayList<>(2);

    /**
     * Write the opCode in the 6 least significant bit
     *
     * @param opCode signed 6-bit integer (value 0-63)
     */
    public void writeOpcode(int opCode) {

        if (opCode < 0 || opCode > 63) {
            LogManager.LOGGER.severe("Couldn't write the opCode for instruction :" + opCode);
        } else {

            //OpCode is the 6 least significant bits
            value &= 0xFFC0; // 1111 1111 1100 0000 mask last 6 bits
            value |= opCode;
        }
    }

    /**
     * Write the source operand in the bits 6-11 (bit 0 being the least significant)
     *
     * @param src signed 5-bit integer (value 0-31)
     */
    public void writeSourceOperand(int src) {

        if (src < 0 || src > 31) {
            LogManager.LOGGER.severe("Couldn't write the scr operand for instruction :" + src);
        } else {

            //Src is the 5 most significant bits
            value &= 0x07FF; //0000 0111 1111 1111
            src <<= 11; //XXXX X000 0000 0000
            value |= src;
        }
    }

    /**
     * Write the destination operand in the 5 most significant bits
     *
     * @param dst signed 5-bit integer (value 0-31)
     */
    public void writeDestinationOperand(int dst) {
        if (dst < 0 || dst > 31) {
            LogManager.LOGGER.severe("Couldn't write the dst operand for instruction :" + dst);
        } else {

            value &= 0xF83F; //1111 1000 0011 1111
            dst <<= 6; //0000 0XXX XX00 0000
            value |= dst;
        }
    }

    public void appendWord(char word) {
        additionalWords.add(word);
    }

    /**
     * Get the bytes of the code
     */
    public byte[] bytes() {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(value >> 8);
        out.write(value);

        for (Character s : additionalWords) {
            out.write(s >> 8);
            out.write(s);
        }

        return out.toByteArray();
    }

}
