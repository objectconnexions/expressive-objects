package uk.co.objectconnexions.expressiveobjects.datagenerator;

public class DataGenerationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataGenerationException(String message) {
        super(message);
    }

    
}
