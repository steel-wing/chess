package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinGame extends UserGameCommand {
    final Integer gameID;
    final ChessGame.TeamColor playerColor;
    public JoinGame(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public Integer getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
