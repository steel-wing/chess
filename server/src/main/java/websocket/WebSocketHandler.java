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
        AuthData auth = getAuth(authToken);
        String message = auth.username() + " has joined the game as the " + command.getPlayerColor() + " team.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, notification);
    }

    private void joinobserver (JoinObserver command, Session session) throws IOException {
        String authToken = command.getAuthString();
        connections.add(authToken, session);
        AuthData auth = getAuth(authToken);
        String message = auth.username() + " is now observing the game.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, notification);
    }

    private void makemove (MakeMove command) throws IOException {
        String authToken = command.getAuthString();
        AuthData auth = getAuth(authToken);
        ChessMove move = command.getMove();
        String message;

        if (move.getPromotionPiece() != null) {
            message = auth.username() + " has made promotion: " + move.getStartPosition().toFancyString()
            + " to " + move.getEndPosition().toFancyString();
        } else {
            message = auth.username() + " has made move: " + move.getStartPosition() + " to " + move.getEndPosition();
        }

        Notification notification = new Notification(message);
        connections.broadcast(authToken, notification);
    }

    private void leave(Leave command) throws IOException {
        String authToken = command.getAuthString();
        connections.remove(authToken);
        AuthData auth = getAuth(authToken);
        String message = auth.username() + " has left the game.";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, notification);
    }

    private void resign(Resign command) throws IOException {
        String authToken = command.getAuthString();
        connections.remove(authToken);
        AuthData auth = getAuth(authToken);
        String message = auth.username() + " has resigned from the game. GG everyone";
        Notification notification = new Notification(message);
        connections.broadcast(authToken, notification);
    }

    private AuthData getAuth(String authToken) throws IOException {
        AuthDAO authDAO = new DatabaseAuthDAO();
        AuthData auth;
        try {
            auth = authDAO.getAuth(authToken);
        } catch (DataAccessException exception) {
            throw new IOException(exception.getMessage());
        }
        return auth;
    }
}