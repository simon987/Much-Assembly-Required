package net.simon987.server.crypto;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {

    private static final String KEY_GENERATION_ALGORITHM = "HmacSHA1";
    private KeyGenerator keyGen;

    public SecretKeyGenerator() {
        try {
            keyGen = KeyGenerator.getInstance(KEY_GENERATION_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error creating Key generator", e);
        }
        keyGen.init(new SecureRandom(SecureRandom.getSeed(32)));
    }

    public String generate() {
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}
