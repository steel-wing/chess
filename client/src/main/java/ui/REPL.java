package ui;

import chess.ChessGame;
import ui.menus.Postlogin;
import ui.menus.Prelogin;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import websocket.MessageHandler;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class REPL implements MessageHandler {
    private final ChessClient client;
    private State oldstate = null;

    public REPL(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String result = "";

        // primary loop machine
        while (!result.equals("Exiting...")) {

            // handles the different kinds of ways to join a game
            String joinType = switch (client.team) {
                case "WHITE" -> "the White Team";
                case "BLACK" -> "the Black Team";
                default -> "an observer";
            };

            // handle extending salutations when relevant
            if (oldstate != client.state) {
                String welcomeString = switch (client.state) {
                    case LOGGEDOUT -> "Welcome to the CS 240 Chess Client, by Davis Wing\n" + Prelogin.help();
                    case LOGGEDIN -> "Welcome, " + client.username + " \n" + Postlogin.help();
                    case GAMEPLAY -> "Joined Game \"" + client.game.gameName() + "\" as " + joinType + "\n";
                };
                System.out.println(welcomeString);
                oldstate = client.state;
            }

            // print out the prompt for the next action and listen
            System.out.print(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + ">>> " + RESET);
            String line = scanner.nextLine();

            // switch between menus
            try {
                result = switch (client.state) {
                    case LOGGEDOUT -> client.prelogin(line);
                    case LOGGEDIN -> client.postlogin(line);
                    case GAMEPLAY -> client.gameplay(line);
                };

                // print out what came from above
                System.out.print(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + result + "\n");

            } catch (Throwable exception) {
                System.out.print(exception.getMessage());
            }
        }
    }

    // this is what handles the different incoming ws messages from the server,
    // since the repl acts as a messagehandler
    @Override
    public void notify(ServerMessage message) {
        String out;
        // handles the case where we're just recieving a notification
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            Notification notification = (Notification) message;
            out = "\n" + notification.getMessage();
        }

        // handles the case where we're recieving some new GameData
        else if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGame loadGame = (LoadGame) message;
            client.game = loadGame.getGame();
            ChessGame.TeamColor team = client.team.equals("BLACK") ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            out = "\n" + RESET + SET_TEXT_COLOR_WHITE + loadGame.getGame().game().toString(team);
        }

        // handles printing errors (in case that was needed?)
        else if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            Error error = (Error) message;
            out = "\n" + error.getErrorMessage();
        }

        // handles whatever else I've missed
        else {
            out = "\n" + message + "\n";
        }

        // print out what we found
        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + out);
        System.out.print(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + ">>> " + RESET);
    }
}
