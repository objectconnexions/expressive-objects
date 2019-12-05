package uk.co.objectconnexions.expressiveobjects.applib.user;

public class PasswordException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PasswordException(Exception e) {
        super(e);
    }

    public PasswordException(String message) {
        super(message);
    }

    public PasswordException(String message, Exception e) {
        super(message, e);
    }

}
