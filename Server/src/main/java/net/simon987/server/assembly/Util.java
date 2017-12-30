package net.simon987.server.assembly;

/**
 * Set of utility functions related to assembly language parsing and execution
 */
public class Util {

    /**
     * Get the lower byte of a word
     */
    static byte getLowerByte(char value) {
        return (byte) (value & 0x00FF);
    }

    /**
     * Get the higher byte of a word
     */
    public static byte getHigherByte(char value) {
        return (byte) ((value >> 8) & 0x00FF);
    }

    public static char getHigherWord(int value) {
        return (char) ((value >> 16) & 0x0000FFFF);
    }

    public static char getLowerWord(int value) {
        return (char) (value & 0x0000FFFF);
    }

    /**
     * Convert a 32bit value to a unsigned 8bit value
     */
    public static int uByte(int b) {
        return (b) & 0x00FF;
    }

    /**
     * Convert a 32bit value to a unsigned 16bit value
     */
    private static int uShort(int s) {
        return s & 0x0000FFFF;
    }

    public static String toHex(int a) {
        return String.format("%04X ", a);
    }

    public static String toHex(byte[] byteArray) {

        String result = "";

        int count = 0;

        for (byte b : byteArray) {
            result += String.format("%02X ", b);
            if (count == 16) {
                count = -1;
                result += "\n";
            }
            count++;
        }

        return result;
    }

    /**
     * Check if a 16bit value is negative using two's complement representation
     *
     * @param result 16bit integer
     */
    public static boolean checkSign16(int result) {
        result = Util.uShort(result);
        return (result >> 15) == 1;
    }

    /**
     * Check if the overflow flag should be set for an
     * add operation.
     *
     * @param a summand
     * @param b summand
     */
    public static boolean checkOverFlowAdd16(int a, int b) {
        boolean aSign = (Util.uShort(a) >> 15) == 1;
        boolean bSign = (Util.uShort(b) >> 15) == 1;

        return aSign == bSign && aSign != checkSign16(a + b);
    }

    /**
     * Check if the overflow flag should be set for a
     * sub operation.
     *
     * @param a minuend
     * @param b subtrahend
     */
    public static boolean checkOverFlowSub16(int a, int b) {
        return checkOverFlowAdd16(a, -b);
    }

    /**
     * Check if the carry flag should be set
     *
     * @param result Result of a 16bit operation
     */
    public static boolean checkCarry16(int result) {
        return ((result) & 0x10000) == 0x10000; //Check if 17th bit is set
    }


    public static int manhattanDist(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);

    }
}
