package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseDAO.DatabaseAuthDAO;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> joinplayer((JoinGame) command, session);
            case JOIN_OBSERVER -> joinobserver((JoinObserver) command, session);
            case MAKE_MOVE -> makemove((MakeMove) command);
            case LEAVE -> leave((Leave) command);
            case RESIGN -> resign((Resign) command);
        }
    }

    private void joinplayer (JoinGame command, Session session) throws IOException {
        String authToken = command.getAuthString();
        connections.add(authToken, session);
        String message = getUsername(authToken) + " has joined the game as the " + command.getPlayerColor() + " team.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, notification);
    }

    private void joinobserver (JoinObserver command, Session session) throws IOException {
        String authToken = command.getAuthString();
        connections.add(authToken, session);
        String message = getUsername(authToken) + " is now observing the game.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, notification);
    }

    private void makemove (MakeMove command) throws IOException {
        String authToken = command.getAuthString();
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

        Notification notification = new Notification(message);
        connections.broadcast(authToken, notification);
    }

    private void leave(Leave command) throws IOException {
        String authToken = command.getAuthString();
        connections.remove(authToken);
        String message = getUsername(authToken) + " has left the game.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, notification);
    }

    private void resign(Resign command) throws IOException {
        String authToken = command.getAuthString();
        connections.remove(authToken);
        String message = getUsername(authToken) + " has resigned from the game.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, notification);
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