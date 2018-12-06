package net.simon987.constructionplugin;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class BluePrintUtil {

    private static byte[] secretKey;
    private static final String SHA512 = "SHA-512";

    //We need 1024 chars = 2048 bytes = 32 values
    private static final char[] ARBITRARY_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456".toCharArray();

    static void setSecretKey(String secretKey) {
        BluePrintUtil.secretKey = secretKey.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Hash a message using SHA512 with the server secret key as the sal
     *
     * @return 128-bit char array
     */
    private static char[] hashMessage(String message) {

        try {
            MessageDigest md = MessageDigest.getInstance(SHA512);
            md.update(secretKey);
            md.update(message.getBytes(StandardCharsets.UTF_8));

            byte[] digest = md.digest();
            char[] chars = new char[digest.length / 2];
            ByteBuffer.wrap(digest).order(ByteOrder.BIG_ENDIAN).asCharBuffer().get(chars);

            return chars;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns a char array representation of a blueprint. It is obtained by hashing the blueprint
     * properties with the server secret key. Some arbitrary values are added to make a 1024-char
     * array. The same blueprint and secret key always gives the same result.
     */
    static char[] bluePrintData(Class<? extends BluePrint> blueprint) {

        char[] result = new char[ARBITRARY_STRING.length * 32];

        for (int i = ARBITRARY_STRING.length - 1; i > 0; --i) {
            char[] hashedBlueprint = hashMessage(ARBITRARY_STRING[i] + blueprint.getName());
            if (hashedBlueprint != null) {
                System.arraycopy(hashedBlueprint, 0, result,
                        i * hashedBlueprint.length, hashedBlueprint.length);
            }
        }

        return result;
    }

}
