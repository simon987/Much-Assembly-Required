package net.simon987.server.crypto;


public class InvalidKeyException extends CryptoException {

    public InvalidKeyException () {

    }

    public InvalidKeyException (String message) {
        super (message);
    }

    public InvalidKeyException (Throwable cause) {
        super (cause);
    }

    public InvalidKeyException (String message, Throwable cause) {
        super (message, cause);
    }

}