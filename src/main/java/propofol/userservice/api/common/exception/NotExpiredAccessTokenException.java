package propofol.userservice.api.common.exception;

public class NotExpiredAccessTokenException extends RuntimeException{
    public NotExpiredAccessTokenException() {
        super();
    }

    public NotExpiredAccessTokenException(String message) {
        super(message);
    }

    public NotExpiredAccessTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExpiredAccessTokenException(Throwable cause) {
        super(cause);
    }

    protected NotExpiredAccessTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
