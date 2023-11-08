package cz.cvut.kbss.ear.ms.exceptions;

public class MSException extends RuntimeException{

    public MSException(String message) {
        super(message);
    }

    public MSException(String message, Throwable cause) {
        super(message, cause);
    }

    public MSException(Throwable cause) {
        super(cause);
    }
}
