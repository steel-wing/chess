package ui;

import chess.ChessGame;
import exception.ResponseException;
import ui.menus.Postlogin;
import ui.menus.Prelogin;

import java.util.HashMap;
import java.util.Map;

public class ChessClient {
    public String username = null;
    public String authToken = null;
    public String gamename = null;
    public ChessGame.TeamColor team = null;
    public Map<Integer, Integer> gamelist = new HashMap<>();
    public final ServerFacade serverface;

    public State state;

    public ChessClient(int serverPort, REPL repl) {
        serverface = new ServerFacade(serverPort);
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
                default -> Postlogin.help(this);
                case "l" -> Postlogin.list(this);
                case "c" -> Postlogin.create(this);
                //case "j" -> join;
                //case "w" -> observe();
                case "x" -> Postlogin.logout(this);
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }


//        // usertype is "the black team" or "the white team" or "an observer"
//        System.out.println("You have entered game: " + gameNumber + " as " + usertype + "\n");
//        System.out.println("Please select one of the following options
}