package propofol.userservice.api.common.exception;

public class SaveProfileException extends Exception {
    public SaveProfileException() {
        super();
    }

    public SaveProfileException(String message) {
        super(message);
    }

    public SaveProfileException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveProfileException(Throwable cause) {
        super(cause);
    }

    protected SaveProfileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
