package exception;

public class ResponseException extends Exception {
    final private int responseCode;

    public ResponseException(int response, String message) {
        super(message);
        this.responseCode = response;
    }

    public int StatusCode() {
        return responseCode;
    }
}
