package propofol.userservice.api.common.exception;

public class ReCreateJwtException extends RuntimeException{
    public ReCreateJwtException() {
        super();
    }

    public ReCreateJwtException(String message) {
        super(message);
    }

    public ReCreateJwtException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReCreateJwtException(Throwable cause) {
        super(cause);
    }

    protected ReCreateJwtException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
