package ui;

import exception.ResponseException;
import model.GameData;
import ui.menus.Gameplay;
import ui.menus.Postlogin;
import ui.menus.Prelogin;

import java.util.HashMap;
import java.util.Map;

public class ChessClient {
    public String username = null;
    public String authToken = null;
    public GameData game;
    public String team = "an observer";
    public Map<Integer, Integer> gameList = new HashMap<>();
    public Map<Integer, GameData> gameDataList = new HashMap<>();
    public final ServerFacade serverFace;
    public State state;

    public ChessClient(int serverPort, REPL repl) {
        serverFace = new ServerFacade(serverPort);
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
                case "x" -> Postlogin.logout(this);
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
        return switch (input.toLowerCase()) {
            default -> Gameplay.help();
            case "d" -> Gameplay.display(this);
            case "x" -> Gameplay.exit(this);
        };
    }
}