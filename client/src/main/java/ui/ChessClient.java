package ui;

import exception.ResponseException;
import model.GameData;
import ui.menus.Gameplay;
import ui.menus.Postlogin;
import ui.menus.Prelogin;
import websocket.MessageHandler;
import websocket.WebSocketClient;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an individual who is interacting with the chess server.
 * It handles all the interactions that they can have, and prints all of the interfaces they see.
 */
public class ChessClient {
    public String username;
    public String authToken;
    public GameData game;
    public String team = "an observer";
    public Map<Integer, Integer> gameList = new HashMap<>();
    public Map<Integer, GameData> gameDataList = new HashMap<>();
    public final ServerFacade serverFace;
    public final String serverUrl;
    public State state;
    public final MessageHandler messageHandler;
    public WebSocketClient webSocketClient;

    public ChessClient(String serverUrl, MessageHandler messageHandler) {
        this.serverUrl = serverUrl;
        serverFace = new ServerFacade(serverUrl);
        this.messageHandler = messageHandler;
        state = State.LOGGEDOUT;
    }

    /**
     * Handles the prelogin menu
     * @param input What was caught by the REPL and passed in here
     * @return The output of whatever function was accessed
     */
    public String prelogin(String input) {
        try {
            return switch (input.toLowerCase()) {
                default -> Prelogin.help();
                case "l" -> Prelogin.login(this);
                case "r" -> Prelogin.register(this);
                case "x", "quit" -> "Exiting...";
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    /**
     * Handles the postlogin menu
     * @param input What was caught by the REPL and passed in here
     * @return The output of whatever function was accessed
     */
    public String postlogin(String input) {
        try {
            return switch (input.toLowerCase()) {
                default -> Postlogin.help();
                case "l" -> Postlogin.list(this);
                case "c" -> Postlogin.create(this);
                case "j" -> Postlogin.join(this);
                case "w" -> Postlogin.observe(this);
                case "x", "quit" -> Postlogin.logout(this);
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    /**
     * Handles the gameplay menu
     * @param input What was caught by the REPL and passed in here
     * @return The output of whatever function was accessed
     */
    public String gameplay(String input) {
        try {
            return switch (input.toLowerCase()) {
                default -> Gameplay.help();
                case "p" -> Gameplay.redraw(this);
                case "m" -> Gameplay.makeMove(this);
                case "s" -> Gameplay.validMoves(this);
                case "r" -> Gameplay.resign(this);
                case "x", "quit" -> Gameplay.exit(this);
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
}