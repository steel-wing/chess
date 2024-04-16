package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
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
    // table of gameIDs to list of authTokens
    public final ConcurrentHashMap<Integer, ArrayList<String>> gameTable = new ConcurrentHashMap<>();

    // table of authTokens to connections
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    // loads a new connection under the authToken for this person,
    // and loads that authToken under the gameID for this game
    public void add(String authToken, Integer gameID, Session session) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
        gameTable.computeIfAbsent(gameID, table -> new ArrayList<>());
        gameTable.get(gameID).add(authToken);
    }

    // removes a connection and that person's place in their game
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

        // if it's an error, redirect it to the root client and return
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            target(excludeAuthToken, gameID, message);
            return;
        }

        // send the info out to all who should see it
        for (Connection connection : connections.values()) {
            // some fancy logic to ensure that no websockets that have been closed are notified
            if (connection.session.isOpen()) {
                // some fancy logic to ensure that only those who are in the game are notified
                if (!connection.authToken.equals(excludeAuthToken) && gameTable.get(gameID).contains(connection.authToken)) {
                    connection.send(new Gson().toJson(message));
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

        // send the message
        Connection connection = connections.get(authToken);
        if (connection.session.isOpen()) {
            connection.send(new Gson().toJson(message));
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

        // if it's an error, redirect it to the root client and return
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            target(authToken, gameID, message);
            return;
        }

        for (Connection connection : connections.values()) {
            // some fancy logic to ensure that no websockets that have been closed are notified
            if (connection.session.isOpen()) {
                // some fancy logic to ensure that only those who are in the game are notified
                if (gameTable.get(gameID).contains(connection.authToken)) {
                    connection.send(new Gson().toJson(message));
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
}


