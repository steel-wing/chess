package dataAccess;

/**
 * Indicates that someone is unauthorized
 */
public class ErrorException extends Exception{
    public ErrorException(String message) {
        super(message);
    }
}