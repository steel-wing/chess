package webSocketMessages.userCommands;

public class JoinObserver extends UserGameCommand {
    final Integer gameID;
    public JoinObserver(String authToken, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }
}
