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

// Client -> REPL -> ChessClient -> WebSocketClient -> Internet
// Internet -> Server -> WebSocketHandler -> ConnectionManager -> Client -> REPL

/**
 * This class handles sending information to the clients
 */
public class ConnectionManager {
    // this lets us keep track of who is in/observing which games
    // It's a table of GameIDs to lists of authTokens
    public final ConcurrentHashMap<Integer, ArrayList<String>> gameTable = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, Integer gameID, Session session) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
        gameTable.computeIfAbsent(gameID, table -> new ArrayList<>());
        gameTable.get(gameID).add(authToken);

    }

    public void remove(String authToken, Integer gameID) {
        connections.remove(authToken);
        gameTable.get(gameID).remove(authToken);
    }

    /**
     * Handles the broadcasting of WebSocket interactions from the server to the players
     * Only sends information to those within a game, by virtue of the gameID parameter
     * @param excludeAuthToken the root client
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

        // if it's an error, redirect it to the root client and return
        if (type == ServerMessage.ServerMessageType.ERROR) {
            target(excludeAuthToken, gameID, message);
            return;
        }

        for (Connection connection : connections.values()) {
            // some fancy logic to ensure that no websockets that have been closed are notified
            if (connection.session.isOpen()) {
                // some fancy logic to ensure that only those who are in the game are notified
                if (!connection.authToken.equals(excludeAuthToken) && gameTable.get(gameID).contains(excludeAuthToken)) {
                    connection.send(outgoing);
                }
            } else {
                removeList.add(connection);
            }
        }

        // Clean up any connections that no longer exist
        for (Connection connection : removeList) {
            connections.remove(connection.authToken);
        }
    }

    /**
     * Sends a message to the specified authToken
     * @param authToken The root client
     * @param gameID The game in question
     * @param message The message to be sent
     * @throws IOException in case of trouble
     */
    public void target(String authToken, Integer gameID, ServerMessage message) throws IOException {
        ServerMessage.ServerMessageType type = message.getServerMessageType();

        // handle the stringification of the message here
        String outgoing = switch (type) {
            case NOTIFICATION -> notification((Notification) message);
            case LOAD_GAME -> loadGame((LoadGame) message);
            case ERROR -> error((ServerError) message);
        };

        // send the message
        Connection connection = connections.get(authToken);
        if (connection.session.isOpen()) {
            connection.send(outgoing);
        } else {
            // if they are no longer connected, remove them
            connections.remove(connection.authToken);
            gameTable.get(gameID).remove(authToken);
        }
    }

    /**
     * Tells everyone in a game what's going on
     * @param authToken The root client
     * @param gameID The game to be notified
     * @param message The message to be sent
     * @throws IOException in case of trouble
     */
    public void slashAll(String authToken, Integer gameID, ServerMessage message) throws IOException {
        ArrayList<Connection> removeList = new ArrayList<>();
        ServerMessage.ServerMessageType type = message.getServerMessageType();

        // handle the stringification of the message here
        String outgoing = switch (type) {
            case NOTIFICATION -> notification((Notification) message);
            case LOAD_GAME -> loadGame((LoadGame) message);
            case ERROR -> error((ServerError) message);
        };

        // if it's an error, redirect it to the root client and return
        if (type == ServerMessage.ServerMessageType.ERROR) {
            target(authToken, gameID, message);
            return;
        }

        for (Connection connection : connections.values()) {
            // some fancy logic to ensure that no websockets that have been closed are notified
            if (connection.session.isOpen()) {
                connection.send(outgoing);
            } else {
                removeList.add(connection);
            }
        }

        // Clean up any connections that no longer exist
        for (Connection connection : removeList) {
            connections.remove(connection.authToken);
        }
    }

    /**
     * These are the messages to be broadcast to clients. They are not requests, but Strings or GameData.
     */

    // sends the plain text notification as written in WebSocketHandler (which is calling this class)
    private String notification(Notification notification) {
        return notification.getMessage();
    }

    // sends the plain text error as given by whatever is calling this.
    private String error(ServerError error) {
        return error.getErrorMessage();
    }

    // sends a GameData object in JSON
    private String loadGame(LoadGame loadgame) {
        return new Gson().toJson(loadgame.getGame());
    }

}


