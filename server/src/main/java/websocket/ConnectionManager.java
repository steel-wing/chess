package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerError;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {


    // this lets us keep track of who is in/observing which games
    // It's a table of GameIDs to lists of authTokens
    public final ConcurrentHashMap<Integer, ArrayList<String>> gameTable = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, Integer gameID, Session session) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
    }

    public void remove(String authToken, Integer gameID) {
        connections.remove(authToken);
    }

    /**
     * Handles the broadcasting of WebSocket interactions from the server to the players
     * Only sends information to those within a game, by virtue of the gameID parameter
     * @param excludeAuthToken who not to send it to
     * @param gameID the game where everyone is
     * @param message what is to be sent
     * @throws IOException in case of emergency
     */
    public void broadcast(String excludeAuthToken, Integer gameID, ServerMessage message) throws IOException {
        ArrayList<Connection> removeList = new ArrayList<>();
        ServerMessage.ServerMessageType type = message.getServerMessageType();

        // handle the stringification of the message here
        String outgoing = switch (type) {
            case NOTIFICATION -> notification((Notification) message);
            case LOAD_GAME -> loadGame((LoadGame) message);
            case ERROR -> error((ServerError) message);
        };

        if (type == ServerMessage.ServerMessageType.ERROR) {

        }

        for (Connection connection : connections.values()) {
            if (connection.session.isOpen()) {
                if (!connection.authToken.equals(excludeAuthToken)) {
                    connection.send(outgoing);
                }
            } else {
                removeList.add(connection);
            }
        }

        // Clean up any connections that were left open.
        for (Connection connection : removeList) {
            connections.remove(connection.authToken);
        }
    }

    // sends the plain text notification as written in WebSocketHandler (which is calling this class)
    private String notification(Notification notification) {
        return notification.getMessage();
    }

    private String error(ServerError error) {
        return error.getErrorMessage();
    }

    // sends a ChessGame object in JSON
    private String loadGame(LoadGame loadgame) {
        return new Gson().toJson(loadgame.getGame());
    }

}


