package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.MakeMove;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class handles sending websocket messages to the server.
 * Whenever a client wishes to communicate with all the others, this is the one that gets called
 */
//need to extend Endpoint for websocket to work properly
public class WebSocketClient extends Endpoint {
    Session session;
    MessageHandler messageHandler;

    public WebSocketClient(String url, MessageHandler messageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.messageHandler = messageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new javax.websocket.MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String incoming) {
                    ServerMessage message = new Gson().fromJson(incoming, ServerMessage.class);
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

    /////////////////////
    // SERVER MESSAGES //
    /////////////////////

//    public void loadGame(String authToken, Integer gameID, String teamColor) throws ResponseException {
//        try {
//            UserGameCommand command = new LoadGame(authToken, gameID, teamColor);
//            this.session.getBasicRemote().sendText(new Gson().toJson(command));
//        } catch (IOException exception) {
//            throw new ResponseException(500, exception.getMessage());
//        }
//    }

    ///////////////////
    // USER COMMANDS //
    ///////////////////

    public void joinplayer (String authToken, Integer gameID, String teamColor) throws ResponseException {
        try {
            UserGameCommand command = new JoinPlayer(authToken, gameID, teamColor);
            send(new Gson().toJson(command));
        } catch (IOException exception) {
            throw new ResponseException(500, exception.getMessage());
        }
    }

    public void joinobserver (String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new JoinObserver(authToken, gameID);
            send(new Gson().toJson(command));
        } catch (IOException exception) {
            throw new ResponseException(500, exception.getMessage());
        }
    }

    public void makemove (String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            UserGameCommand command = new MakeMove(authToken, gameID, move);
            send(new Gson().toJson(command));
        } catch (IOException exception) {
            throw new ResponseException(500, exception.getMessage());
        }
    }


//    public void enterPetShop(String authToken) throws ResponseException {
//        try {
//            var action = new UserGameCommand(authToken);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//
//    public void leavePetShop(String authToken) throws ResponseException {
//        try {
//            var action = new UserGameCommand(authToken);
//            // hmmm we'll need to figure this out.
//            //Action.CommandType = LEAVE;
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//            this.session.close();
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }

}

