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
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import java.io.IOException;

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
        System.out.println(command);
        System.out.println(command.getCommandType());

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

    private void joinplayer (JoinPlayer command, Session session) throws IOException, DataAccessException {
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();
        connections.add(authToken, gameID, session);

        // send LOAD_GAME message to root client
        GameDAO gameDAO = new DatabaseGameDAO();
        GameData game = gameDAO.getGame(gameID);
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
        connections.add(authToken, gameID, session);

        // send LOAD_GAME message to root client
        GameDAO gameDAO = new DatabaseGameDAO();
        GameData game = gameDAO.getGame(gameID);
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
        String message;

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
        GameData old = gameDAO.getGame(gameID);

        // move validity check
        if (!old.game().validMoves(move.getStartPosition()).contains(move)) {
            throw new IOException("Invalid Move Requested");
        }

        // get the checks before the move is made
        boolean whitecheck = gameDAO.getGame(gameID).game().isInCheck(ChessGame.TeamColor.WHITE);
        boolean blackcheck = gameDAO.getGame(gameID).game().isInCheck(ChessGame.TeamColor.BLACK);

        // make the move
        try {
            System.out.println("debug");
            System.out.println(old.game().getBoard());
            old.game().makeMove(move);
            System.out.println(old.game().getBoard());
        } catch (InvalidMoveException exception) {
            throw new DataAccessException("Move could not be made");
        }

        // handle all of the horrible updating and handling of notifications
        whitecheck = !whitecheck && gameDAO.getGame(gameID).game().isInCheck(ChessGame.TeamColor.WHITE);
        blackcheck = !blackcheck && gameDAO.getGame(gameID).game().isInCheck(ChessGame.TeamColor.BLACK);
        boolean blackwins = gameDAO.getGame(gameID).game().isInCheckmate(ChessGame.TeamColor.WHITE);
        boolean whitewins = gameDAO.getGame(gameID).game().isInCheckmate(ChessGame.TeamColor.BLACK);

        if (blackwins) {
            old.game().setWinner(old.blackUsername());
        }
        if (whitewins) {
            old.game().setWinner(old.whiteUsername());
        }

        gameDAO.updateGame(gameID, new GameData(gameID, old.whiteUsername(), old.blackUsername(), old.gameName(), old.game()));

        // send a LOAD_GAME to all clients
        LoadGame loadGame = new LoadGame(old);
        connections.slashAll(authToken, gameID, loadGame);

        // send a NOTIFICATION to all other clients that the root client has made a move
        Notification notification = new Notification(message);
        connections.broadcast(authToken, gameID, notification);

        // if the move resulted in check or mate, send a NOTIFICATION to all as well
        if (whitecheck) {
            Notification wcheck = new Notification("White is now in check");
            connections.slashAll(authToken, gameID, wcheck);
        }
        if (blackcheck) {
            Notification bcheck = new Notification("Black is now in check");
            connections.slashAll(authToken, gameID, bcheck);
        }
        if (blackwins) {
            Notification wmate = new Notification("White is in checkmate. Black wins!");
            connections.slashAll(authToken, gameID, wmate);
        }
        if (whitewins) {
            Notification bmate = new Notification("Black is in checkmate. White wins!");
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
        GameData old = gameDAO.getGame(gameID);

        // get the opponent's name and set them as the winner
        if (username.equals(old.whiteUsername())) {
            old.game().setWinner(old.blackUsername());
        } else {
            old.game().setWinner(old.blackUsername());
        }

        // send a NOTIFICATION to everyone that the root client has resigned
        String message = username + " has resigned from the game.";
        Notification notification = new Notification(message);
        connections.slashAll(authToken, gameID, notification);
    }

    /**
     * Helpful function for getting usernames from authTokens
     * @param authToken the authToken in question
     * @return The requested username
     * @throws IOException In case of trouble
     */
    private String getUsername(String authToken) throws IOException {
        AuthDAO authDAO = new DatabaseAuthDAO();
        AuthData auth;
        try {
            auth = authDAO.getAuth(authToken);
        } catch (DataAccessException exception) {
            throw new IOException(exception.getMessage());
        }
        return auth.username();
    }
}