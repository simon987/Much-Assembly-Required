package net.simon987.mar.server.crypto;


public class CryptoException extends Exception {

    public CryptoException () {

    }

    public CryptoException (String message) {
        super (message);
    }

    public CryptoException (Throwable cause) {
        super (cause);
    }

    public CryptoException (String message, Throwable cause) {
        super (message, cause);
    }

}
