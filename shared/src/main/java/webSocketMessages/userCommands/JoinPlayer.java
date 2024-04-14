package webSocketMessages.userCommands;

public class JoinPlayer extends UserGameCommand {
    final Integer gameID;
    final String playerColor;
    public JoinPlayer(String authToken, Integer gameID, String playerColor) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public Integer getGameID() {
        return gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}
