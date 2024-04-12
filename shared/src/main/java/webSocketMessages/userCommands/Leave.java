package webSocketMessages.userCommands;

public class Leave extends UserGameCommand {
    final Integer gameID;
    public Leave(String authToken, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }
}
