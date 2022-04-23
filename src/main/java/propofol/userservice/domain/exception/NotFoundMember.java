package propofol.userservice.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundMember extends NoSuchElementException {
    public NotFoundMember() {
        super();
    }

    public NotFoundMember(String s) {
        super(s);
    }
}
