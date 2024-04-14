package webSocketMessages.serverMessages;

public class ServerError extends ServerMessage {
    final String errorMessage;
    public ServerError(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
