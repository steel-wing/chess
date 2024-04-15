package websocket;


import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerError;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

// Client -> REPL -> ChessClient -> WebSocketClient -> Internet
// Internet -> Server -> WebSocketHandler -> ConnectionManager -> Client -> REPL

/**
 * This class handles sending outgoing websocket messages to the server.
 * Whenever a client wishes to communicate with the server, this is the one that gets called first.
 * It takes the context and sends commands through WebSocket to the Server, which in turn
 * sends them to the WebSocketHandler
 */
//need to extend Endpoint for websocket to work properly
public class WebSocketClient extends Endpoint {
    Session session;
    MessageHandler messageHandler;

    public WebSocketClient(String url, MessageHandler messageHandler) throws ResponseException {
        System.out.println("HIT");

        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.messageHandler = messageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // watch out for this one on the debug
            this.session.addMessageHandler(new javax.websocket.MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String incoming) {
                    ServerMessage message = new Gson().fromJson(incoming, ServerMessage.class);
                    switch (message.getServerMessageType()) {
                        case NOTIFICATION -> message = new Gson().fromJson(incoming, Notification.class);
                        case LOAD_GAME -> message = new Gson().fromJson(incoming, LoadGame.class);
                        case ERROR -> message = new Gson().fromJson(incoming, ServerError.class);
                    }
                    messageHandler.notify(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    /**
     * Required to be here by the /connect endpoint.
     * @param session The current connection + authToken
     * @param endpointConfig No freaking clue
     */
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // System.out.println("Connection established: " + session.getId());
    }

    /**
     * Helpful sender function courtesy of Dr. Rodham
     * @param message What we're wanting to send
     * @throws IOException in case of trouble
     */
    public void send(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    // joins a game as a player
    public void joinplayer (String authToken, Integer gameID, String teamColor) throws ResponseException {
        try {
            UserGameCommand command = new JoinPlayer(authToken, gameID, teamColor);
            send(new Gson().toJson(command));
        } catch (IOException exception) {
            throw new ResponseException(500, exception.getMessage());
        }
    }

    // joins the specified game as an observer
    public void joinobserver (String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new JoinObserver(authToken, gameID);
            send(new Gson().toJson(command));
        } catch (IOException exception) {
            throw new ResponseException(500, exception.getMessage());
        }
    }

    // asks the server to make a move
    public void makemove (String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            UserGameCommand command = new MakeMove(authToken, gameID, move);
            send(new Gson().toJson(command));
        } catch (IOException exception) {
            throw new ResponseException(500, exception.getMessage());
        }
    }

    // resigns: the game is lost, but no one leaves
    public void resign (String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new Resign(authToken, gameID);
            send(new Gson().toJson(command));
        } catch (IOException exception) {
            throw new ResponseException(500, exception.getMessage());
        }
    }

    // exits the actual game: the user is no longer representing the team, and the game is lost
    public void leave (String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new Leave(authToken, gameID);
            send(new Gson().toJson(command));
            this.session.close();
        } catch (IOException exception) {
            throw new ResponseException(500, exception.getMessage());
        }
    }
}

