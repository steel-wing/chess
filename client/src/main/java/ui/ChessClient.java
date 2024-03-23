package ui;

import exception.ResponseException;
import result.LoginResponse;
import server.Server;

import java.util.Arrays;

import static ui.State.LOGGEDOUT;

public class ChessClient {
    private String username = null;
    private String authToken = null;
    private final ServerFacade server;
    private final int serverPort;
    private final REPL repl;
    private State state = LOGGEDOUT;

    public ChessClient(int serverPort, REPL repl) {
        server = new ServerFacade(serverPort);
        this.serverPort = serverPort;
        this.repl = repl;

        // construct and open up a new server on port 8080
        Server server = new Server();
        server.run(serverPort);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "H";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "L" -> login(params);
                case "Q" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 1) {

            LoginResponse res = server.login(params);
            username = res.username();
            authToken = res.authToken();
            state = State.LOGGEDIN;

            return String.format("You signed in as %s.", username);
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String logout() {
        return "loged ot";
    }

    public String help() {
        return """
        Please select one of the following options:
        [H] : Help for understanding functions and commands
        [L] : Login to your Chess Game account
        [R] : Register a new Chess Game account
        [X] : Exit the Chess Client
        """;
    }






//        System.out.println("Welcome " + username + " to the Chess Game Menu\n");
//        System.out.println("Please select one of the following options:");
//        System.out.println("[H] : Help for understanding functions and commands");
//        System.out.println("[L] : List the current running chess games");
//        System.out.println("[C] : Create a new game of chess");
//        System.out.println("[J] : Join an existing game of chess");
//        System.out.println("[W] : Watch an existing game of chess");
//        System.out.println("[X] : Logout and return to the Chess Game Client");


//        // usertype is "the black team" or "the white team" or "an observer"
//        System.out.println("You have entered game: " + gameNumber + " as " + usertype + "\n");
//        System.out.println("Please select one of the following options
}