package exception;

public class ResponseException extends Exception {

    public ResponseException(int response, String message) {
        super(message);
    }
}
