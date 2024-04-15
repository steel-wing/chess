package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseDAO.DatabaseAuthDAO;
import dataAccess.DatabaseDAO.DatabaseGameDAO;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

import java.io.IOException;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

// Client -> REPL -> ChessClient -> WebSocketClient -> Internet
// Internet -> Server -> WebSocketHandler -> ConnectionManager -> Client -> REPL

/**
 * This class handles incoming websocket messages to the server.
 * The WebSocketClient sends requests here, and they are processed.
 * Necessary changes to the games are made, and all relevant clients are notified.
 */
@WebSocket
public class WebSocketHandler {
    // this lets us access the list of all current WebSocket connections
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        // extract the command from the JSON
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        // delegate who gets the command based on what type it is
        // this was tricky: you have to deserialize once to get the class type, then deserialize directly into that class
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> joinplayer(new Gson().fromJson(message, JoinPlayer.class), session);
            case JOIN_OBSERVER -> joinobserver(new Gson().fromJson(message, JoinObserver.class), session);
            case MAKE_MOVE -> makemove(new Gson().fromJson(message, MakeMove.class));
            case LEAVE -> leave(new Gson().fromJson(message, Leave.class));
            case RESIGN -> resign(new Gson().fromJson(message, Resign.class));
        }
    }

    private void joinplayer (JoinPlayer command, Session session) throws IOException{
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();
        String team = command.getPlayerColor();
        String username = getUsername(authToken);
        GameDAO gameDAO = new DatabaseGameDAO();
        GameData game = null;

        connections.add(authToken, gameID, session);

        try {game = gameDAO.getGame(gameID);} catch (DataAccessException ignored) {}

        if (game == null) {
            connections.target(authToken, gameID, new Error("Error: Bad GameID"));
            return;
        }

        if(team.equals("WHITE") && game.whiteUsername() != null && !game.whiteUsername().equals(username)) {
            connections.target(authToken, gameID, new Error("Error: White team already taken"));
            return;
        }

        if ((team.equals("BLACK") && game.blackUsername() != null && !game.blackUsername().equals(username))) {
            connections.target(authToken, gameID, new Error("Error: Black team already taken"));
            return;
        }

        if (team.equals("BLACK") && game.blackUsername() == null || team.equals("WHITE") && game.whiteUsername() == null) {
            connections.target(authToken, gameID, new Error("Error: Empty team accessed"));
            return;
        }

        if (username == null) {
            connections.target(authToken, gameID, new Error("Error: Username not recognized"));
            return;
        }

        // send LOAD_GAME message to root client
        LoadGame loadGame = new LoadGame(game);
        connections.target(authToken, gameID, loadGame);

        // send NOTIFICATION to all other clients
        String message = getUsername(authToken) + " has joined the game as the " + command.getPlayerColor() + " team.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, gameID, notification);
    }

    private void joinobserver (JoinObserver command, Session session) throws IOException, DataAccessException {
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();
        String username = getUsername(authToken);
        GameDAO gameDAO = new DatabaseGameDAO();
        GameData game = null;

        connections.add(authToken, gameID, session);

        try {game = gameDAO.getGame(gameID);} catch (DataAccessException ignored) {}

        if (game == null) {
            connections.target(authToken, gameID, new Error("Error: Bad GameID"));
            return;
        }

        if (username == null) {
            connections.target(authToken, gameID, new Error("Error: Username not recognized"));
            return;
        }

        // send LOAD_GAME message to root client
        LoadGame loadGame = new LoadGame(game);
        connections.target(authToken, gameID, loadGame);

        // send NOTIFICATION to all other clients
        String message = getUsername(authToken) + " is now observing the game.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, gameID, notification);
    }

    private void makemove (MakeMove command) throws IOException, DataAccessException {
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();
        ChessMove move = command.getMove();
        String username = getUsername(authToken);
        String message;
        GameData game = null;

        // little extra detail for handling if there's a pawn promotion
        if (move.getPromotionPiece() != null) {
            message = getUsername(authToken) + " has made promotion: " + move.getStartPosition().toFancyString()
            + " to " + move.getEndPosition().toFancyString();
        } else {
            message = getUsername(authToken) + " has made move: " + move.getStartPosition().toFancyString()
            + " to " + move.getEndPosition().toFancyString();
        }

        // get the game to be updated
        GameDAO gameDAO = new DatabaseGameDAO();
        try {game = gameDAO.getGame(gameID);} catch (DataAccessException ignored) {}

        if (game == null) {
            connections.target(authToken, gameID, new Error("Error: Bad GameID"));
            return;
        }

        if (username == null) {
            connections.target(authToken, gameID, new Error("Error: Username not recognized"));
            return;
        }

        if ((game.blackUsername() != null && !username.equals(game.blackUsername())) &&
            (game.whiteUsername() != null && !username.equals(game.whiteUsername()))) {
            connections.target(authToken, gameID, new Error("Error: Invalid user request"));
            return;
        }

        ChessGame.TeamColor turn = username.equals(game.whiteUsername()) ? WHITE : BLACK;

        if (!game.game().validMoves(move.getStartPosition()).contains(move) ||
            !game.game().getTeamTurn().equals(turn)) {
            connections.target(authToken, gameID, new Error("Error: Invalid Move Requested"));
            return;
        }

        if (game.game().getWinner() != null) {
            connections.target(authToken, gameID, new Error("Error: Game is over: no more moves"));
            return;
        }

        // get the checks before the move is made
        boolean whitecheck = gameDAO.getGame(gameID).game().isInCheck(WHITE);
        boolean blackcheck = gameDAO.getGame(gameID).game().isInCheck(BLACK);

        // make the move
        try {
            game.game().makeMove(move);
        } catch (InvalidMoveException exception) {
            connections.target(authToken, gameID, new Error("Error: Move could not be made"));
            return;
        }

        // handle all of the horrible updating and handling of notifications
        whitecheck = !whitecheck && gameDAO.getGame(gameID).game().isInCheck(WHITE);
        blackcheck = !blackcheck && gameDAO.getGame(gameID).game().isInCheck(BLACK);
        boolean blackwins = gameDAO.getGame(gameID).game().isInCheckmate(WHITE);
        boolean whitewins = gameDAO.getGame(gameID).game().isInCheckmate(BLACK);

        if (blackwins) {
            game.game().setWinner(game.blackUsername());
        }
        if (whitewins) {
            game.game().setWinner(game.whiteUsername());
        }

        gameDAO.updateGame(gameID, new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));

        // send a LOAD_GAME to all clients
        LoadGame loadGame = new LoadGame(game);
        connections.slashAll(authToken, gameID, loadGame);

        // send a NOTIFICATION to all other clients that the root client has made a move
        Notification notification = new Notification(message);
        connections.broadcast(authToken, gameID, notification);

        // if the move resulted in check or mate, send a NOTIFICATION to all as well
        if (whitecheck) {
            Notification wcheck = new Notification(game.whiteUsername() + " is in check");
            connections.slashAll(authToken, gameID, wcheck);
        }
        if (blackcheck) {
            Notification bcheck = new Notification(game.blackUsername() + " is in check");
            connections.slashAll(authToken, gameID, bcheck);
        }
        if (blackwins) {
            Notification wmate = new Notification(game.whiteUsername() + " is in checkmate. " + game.blackUsername() + " wins!");
            connections.slashAll(authToken, gameID, wmate);
        }
        if (whitewins) {
            Notification bmate = new Notification(game.blackUsername() + " is in checkmate. " + game.whiteUsername() + " wins!");
            connections.slashAll(authToken, gameID, bmate);
        }
    }

    private void leave(Leave command) throws IOException, DataAccessException {
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();

        // get the username corresponding to this User
        AuthDAO authDAO = new DatabaseAuthDAO();
        String username = authDAO.getAuth(authToken).username();

        // remove the client from the game
        GameDAO gameDAO = new DatabaseGameDAO();
        GameData old = gameDAO.getGame(gameID);
        // we only remove their username if they were a player
        GameData current = old;
        if (username.equals(old.whiteUsername())) {
            current = new GameData(old.gameID(), null, old.blackUsername(), old.gameName(), old.game());
        }
        if (username.equals(old.blackUsername())) {
            current = new GameData(old.gameID(), old.whiteUsername(), null, old.gameName(), old.game());
        }
        gameDAO.updateGame(old.gameID(), current);

        // send a NOTIFICATION to all other clients that the root client has left
        String message = getUsername(authToken) + " has left the game.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, gameID, notification);
        connections.remove(authToken, gameID);
    }

    private void resign(Resign command) throws IOException, DataAccessException {
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();
        String username = getUsername(authToken);

        GameDAO gameDAO = new DatabaseGameDAO();
        GameData game = gameDAO.getGame(gameID);

        if (username == null) {
            connections.target(authToken, gameID, new Error("Error: Username not found"));
            return;
        }

        if (!username.equals(game.blackUsername()) && !username.equals(game.whiteUsername())) {
            connections.target(authToken, gameID, new Error("Error: Not a player"));
            return;
        }

        if (game.game().getWinner() != null) {
            connections.target(authToken, gameID, new Error("Error: Game is over"));
            return;
        }

        // get the opponent's name and set them as the winner
        if (username.equals(game.whiteUsername())) {
            game.game().setWinner(game.blackUsername());
        } else {
            game.game().setWinner(game.blackUsername());
        }

        // update the change to the game in the database
        gameDAO.updateGame(gameID, game);

        // send a NOTIFICATION to everyone that the root client has resigned
        String message = username + " has resigned from the game." + game.game().getWinner() + " wins!";
        Notification notification = new Notification(message);
        connections.slashAll(authToken, gameID, notification);
    }

    /**
     * Helpful function for getting usernames from authTokens
     * @param authToken the authToken in question
     * @return The requested username
     * @throws IOException In case of trouble
     */
    private String getUsername(String authToken) {
        AuthDAO authDAO = new DatabaseAuthDAO();
        AuthData auth;
        try {
            auth = authDAO.getAuth(authToken);
        } catch (DataAccessException exception) {
            return null;
        }
        return auth.username();
    }
}