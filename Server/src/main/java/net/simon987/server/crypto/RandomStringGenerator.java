/**
 *
 * Based on the RandomString class by erickson:
 * https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
 *
 */

package net.simon987.server.crypto;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RandomStringGenerator {

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = charset[random.nextInt(charset.length)];
        return new String(buf);
    }

    static final String UPPER_ALPHA_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final String LOWER_ALPHA_CHARSET = UPPER_ALPHA_CHARSET.toLowerCase(Locale.ROOT);
    static final String NUMERIC_CHARSET = "0123456789";
    static final String ALPHANUMERIC_CHARSET = UPPER_ALPHA_CHARSET + LOWER_ALPHA_CHARSET + NUMERIC_CHARSET;

    private final Random random;

    private final char[] charset;

    private final char[] buf;

    public RandomStringGenerator(int length, Random random, String charset) {

        if (length < 1) {
            throw new IllegalArgumentException();
        } else if (charset.length() < 2) {
            throw new IllegalArgumentException();
        }
        this.random = Objects.requireNonNull(random);
        this.charset = charset.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Create an alphanumeric string generator.
     */
    public RandomStringGenerator(int length, Random random) {
        this(length, random, ALPHANUMERIC_CHARSET);
    }

    /**
     * Create an alphanumeric string generator using the given charset.
     */
    public RandomStringGenerator(int length, String charset) {
        this(length, new SecureRandom(), charset);
    }


    /**
     * Create an alphanumeric strings from a secure generator.
     */
    public RandomStringGenerator(int length) {
        this(length, new SecureRandom(), ALPHANUMERIC_CHARSET);
    }

    /**
     * Create 8-character alphanumeric strings from a secure generator.
     */
    public RandomStringGenerator() {
        this(8);
    }

}