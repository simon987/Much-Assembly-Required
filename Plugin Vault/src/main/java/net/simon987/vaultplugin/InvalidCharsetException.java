public class InvalidCharsetException extends Exception {
    public InvalidCharsetException () {

    }

    public InvalidCharsetException (String message) {
        super (message);
    }

    public InvalidCharsetException (Throwable cause) {
        super (cause);
    }

    public InvalidCharsetException (String message, Throwable cause) {
        super (message, cause);
    }