package webSocketMessages.serverMessages;

public class Error extends ServerMessage {
    final String errorMessage;
    public Error(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
}
