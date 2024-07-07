package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MemoryDAO.MemoryAuthDAO;
import dataAccess.MemoryDAO.MemoryGameDAO;
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

    /**
     * Handles incoming commands from the
     * @param session The WebSocket session we currently find ourselves in
     * @param message The command sent from the client to us, the server
     * @throws IOException in case of WebSocket connection issues
     * @throws DataAccessException in case of data access issues
     */
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        // extract the command from the JSON
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        // delegate who gets the command based on what type it is
        // this was a little tricky: you have to deserialize once to get the class type, then deserialize directly into that class
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> joinplayer(new Gson().fromJson(message, JoinPlayer.class), session);
            case JOIN_OBSERVER -> joinobserver(new Gson().fromJson(message, JoinObserver.class), session);
            case MAKE_MOVE -> makemove(new Gson().fromJson(message, MakeMove.class));
            case LEAVE -> leave(new Gson().fromJson(message, Leave.class));
            case RESIGN -> resign(new Gson().fromJson(message, Resign.class));
        }
    }

    /**
     * Handles a user entering the gamplay loop as an player
     * @param command command the join command
     * @param session session the WebSocket session (connection) to which we'll be adding the user
     * @throws IOException in case of connection issues
     */
    private void joinplayer (JoinPlayer command, Session session) throws IOException{
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();
        String team = command.getPlayerColor();
        String username = getUsername(authToken);
        GameDAO gameDAO = new MemoryGameDAO();
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

        String teamstring = command.getPlayerColor().equals("WHITE") ? "White" : "Black";

        // send NOTIFICATION to all other clients
        String message = getUsername(authToken) + " has joined the game as the " + teamstring + " team.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, gameID, notification);
    }

    /**
     * Handles a user entering the gamplay loop as an observer
     * @param command the join command
     * @param session the WebSocket session (connection) to which we'll be adding the user
     * @throws IOException in case of connection issues
     */
    private void joinobserver (JoinObserver command, Session session) throws IOException{
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();
        String username = getUsername(authToken);
        GameDAO gameDAO = new MemoryGameDAO();
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

    /**
     * Handles a client making a move. Ensures proper context and all that jazz
     * @param command the make move command sent from the Gameplay loop
     * @throws IOException in case of connection issues
     * @throws DataAccessException in case of data access issues
     */
    private void makemove (MakeMove command) throws IOException, DataAccessException {
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();
        ChessMove move = command.getMove();
        String username = getUsername(authToken);
        GameDAO gameDAO = new MemoryGameDAO();
        GameData game = null;
        String message;

        // get the game to be updated
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
        boolean whitecheck = game.game().isInCheck(WHITE);
        boolean blackcheck = game.game().isInCheck(BLACK);

        // actually make the move
        try {
            game.game().makeMove(move);
        } catch (InvalidMoveException ignored) {
            connections.target(authToken, gameID, new Error("Error: Move could not be made"));
            return;
        }

        // more boolean checks for all the possible end states
        whitecheck = !whitecheck && game.game().isInCheck(WHITE);
        blackcheck = !blackcheck && game.game().isInCheck(BLACK);
        boolean blackwins = game.game().isInCheckmate(WHITE);
        boolean whitewins = game.game().isInCheckmate(BLACK);
        boolean stalemate = game.game().isInStalemate(WHITE) || game.game().isInStalemate(BLACK);

        if (blackwins) {
            game.game().setWinner(game.blackUsername());
        }
        if (whitewins) {
            game.game().setWinner(game.whiteUsername());
        }
        if (stalemate) {
            game.game().setWinner("STALEMATE");
        }

        // send a LOAD_GAME to all clients
        gameDAO.updateGame(gameID, new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));
        LoadGame loadGame = new LoadGame(game);
        connections.slashAll(authToken, gameID, loadGame);

        // send a NOTIFICATION to all other clients that the root client has made a move
        message = getUsername(authToken) + ": " + move.getStartPosition().toFancyString() + " -> " + move.getEndPosition().toFancyString();
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
        if (stalemate) {
            Notification stale = new Notification("The game has ended in a stalemate!");
            connections.slashAll(authToken, gameID, stale);
        }
    }

    /**
     * Handles a client leaving the Gameplay loop
     * @param command the leave command sent from the Gameplay loop
     * @throws IOException in case of connection issues
     * @throws DataAccessException in case of data access issues
     */
    private void leave(Leave command) throws IOException, DataAccessException {
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();
        String username = getUsername(authToken);
        GameDAO gameDAO = new MemoryGameDAO();
        GameData game = gameDAO.getGame(gameID);

        if (username == null) {
            connections.target(authToken, gameID, new Error("Error: Username not found"));
            return;
        }

        // we only remove their username if they were a player
        GameData current = game;
        if (username.equals(game.whiteUsername())) {
            current = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
        }
        if (username.equals(game.blackUsername())) {
            current = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
        }

        // remove the client from the game
        gameDAO.updateGame(game.gameID(), current);

        // send a NOTIFICATION to all other clients that the root client has left
        String message = getUsername(authToken) + " has left the game";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, gameID, notification);
        connections.remove(authToken, gameID);
    }

    /**
     * Handles resignation commands from the client. Ensures that context makes sense
     * @param command the resignation command sent from the Gameplay loop
     * @throws IOException in case of connection issues
     * @throws DataAccessException in case of data access issues
     */
    private void resign(Resign command) throws IOException, DataAccessException {
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();
        String username = getUsername(authToken);
        GameDAO gameDAO = new MemoryGameDAO();
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
            game.game().setWinner(game.whiteUsername());
        }
        // update the change to the game in the database
        gameDAO.updateGame(gameID, game);

        // send a NOTIFICATION to everyone that the root client has resigned
        String message = username + " has resigned from the game. " + game.game().getWinner() + " wins!";
        Notification notification = new Notification(message);
        connections.slashAll(authToken, gameID, notification);
    }

    /**
     * Helpful function for getting usernames from authTokens
     * @param authToken the authToken in question
     * @return The requested username
     */
    private String getUsername(String authToken) {
        AuthDAO authDAO = new MemoryAuthDAO();
        AuthData auth;
        try {
            auth = authDAO.getAuth(authToken);
        } catch (DataAccessException exception) {
            return null;
        }
        return auth.username();
    }
}