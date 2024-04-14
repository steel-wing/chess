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
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

import java.io.IOException;

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
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> joinplayer((JoinPlayer) command, session);
            case JOIN_OBSERVER -> joinobserver((JoinObserver) command, session);
            case MAKE_MOVE -> makemove((MakeMove) command);
            case LEAVE -> leave((Leave) command);
            case RESIGN -> resign((Resign) command);
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
        connections.broadcast(authToken, gameID, loadGame);

        // send NOTIFICATION to all other clients
        String message = getUsername(authToken) + " has joined the game as the " + command.getPlayerColor() + " team.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, gameID, notification);
    }

    private void joinobserver (JoinObserver command, Session session) throws IOException, DataAccessException {
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();
        connections.add(authToken, gameID, session);

        GameDAO gameDAO = new DatabaseGameDAO();
        GameData game = gameDAO.getGame(gameID);
        LoadGame loadGame = new LoadGame(game);
        connections.broadcast(authToken, gameID, loadGame);

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

        // actually go in and update the game
        GameDAO gameDAO = new DatabaseGameDAO();
        GameData old = gameDAO.getGame(gameID);

        boolean whitecheck = gameDAO.getGame(gameID).game().isInCheck(ChessGame.TeamColor.WHITE);
        boolean blackcheck = gameDAO.getGame(gameID).game().isInCheck(ChessGame.TeamColor.BLACK);

        try {
            System.out.println("debug");
            System.out.println(old.game().getBoard());
            old.game().makeMove(move);
            System.out.println(old.game().getBoard());
        } catch (InvalidMoveException exception) {
            throw new DataAccessException("Move could not be made");
        }
        gameDAO.updateGame(gameID, new GameData(gameID, old.whiteUsername(), old.blackUsername(), old.gameName(), old.game()));

        // send a load game to all clients
        LoadGame loadGame = new LoadGame(old);
        connections.slashAll(gameID, loadGame);

        // send a notification to all other clients that the root client has made a move
        Notification notification = new Notification(message);
        connections.broadcast(authToken, gameID, notification);

        // if the move resulted in check or mate, send a notification as well
        if (!whitecheck && gameDAO.getGame(gameID).game().isInCheck(ChessGame.TeamColor.WHITE)) {
            Notification wcheck = new Notification("White is now in check");
            connections.slashAll(gameID, wcheck);
        }
        if (!blackcheck && gameDAO.getGame(gameID).game().isInCheck(ChessGame.TeamColor.BLACK)) {
            Notification bcheck = new Notification("Black is now in check");
            connections.slashAll(gameID, bcheck);
        }
        if (gameDAO.getGame(gameID).game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            Notification wmate = new Notification("White is in checkmate. Black wins!");
            connections.slashAll(gameID, wmate);
        }
        if (gameDAO.getGame(gameID).game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            Notification bmate = new Notification("Black is in checkmate. White wins!");
            connections.slashAll(gameID, bmate);
        }
    }

    private void leave(Leave command) throws IOException {
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();

        // notify all other clients that the root client has left
        String message = getUsername(authToken) + " has left the game.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, gameID, notification);
        connections.remove(authToken, gameID);
    }

    private void resign(Resign command) throws IOException {
        String authToken = command.getAuthString();
        Integer gameID = command.getGameID();

        // notify everyone that the root client has resigned
        String message = getUsername(authToken) + " has resigned from the game.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, gameID, notification);
        connections.target(authToken, gameID, notification);
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