package ui.menus;

import exception.ResponseException;
import model.GameData;
import result.CreateResponse;
import ui.ChessClient;
import ui.State;
import websocket.WebSocketClient;

import java.util.ArrayList;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Postlogin {
    // a helpful little flag for refreshing the list
    static boolean listFlag;
    public static String help() {
        return RESET + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + """
        Please select one of the following options:
        [H] : Help for understanding functions and commands
        [L] : List the current running chess games
        [C] : Create a new game of chess
        [J] : Join an existing game of chess
        [W] : Watch an existing game of chess
        [X] : Logout and return to the Chess Game Client""";
    }

    public static String list(ChessClient client) throws ResponseException {
        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + "Games List:");

        try {
            // retrieve the list of games, store it, and print it out
            ArrayList<GameData> list = client.serverFace.list(client.authToken).games();
            StringBuilder output = new StringBuilder();
            output.append(RESET_TEXT_BOLD_FAINT);

            // roll through and update the number list, and the gamedata registry
            for (int i = 1; i <= list.size(); i++) {
                GameData game = list.get(i - 1);
                client.gameDataList.put(game.gameID(), game);
                client.gameList.put(i, game.gameID());

                String winner = "";
                if (game.game().getWinner() != null) {
                    winner = " -> " + game.game().getWinner();
                }

                // "Game 4: boogerAIDS (W: RickSanchez, B: MortySmith)"
                String linestring = "Game " + i + ": " + game.gameName() + " (W: " + game.whiteUsername() + ", B: " + game.blackUsername() + ")" + winner +"\n";
                output.append(linestring);
            }

            // forbidden newline deletion technique
            if (!list.isEmpty()) {
                output.delete(output.length() - 1, output.length());
            } else {
                output = new StringBuilder();
                output.append("There are no available games");
                listFlag = false;
                return output.toString();
            }

            listFlag = true;
            return output.toString();
        } catch (ResponseException exception) {
            return "Unable to access list: " + exception.getMessage();
        }
    }

    public static String create(ChessClient client) throws ResponseException {
        System.out.println(SET_TEXT_COLOR_BLUE + "Please choose a name for your game:" + RESET);
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] input = line.toLowerCase().split(" ");
        String gamename;

        if (input.length == 1) {
            try {
                CreateResponse response = client.serverFace.create(input[0], client.authToken);
                gamename = input[0];
                listFlag = false;
            } catch (ResponseException exception) {
                return "Unable to create game: " + exception.getMessage();
            }
            return String.format("You have created game: %s", gamename);
        }
        throw new ResponseException(400, "Format: <gamename>\n");
    }

    public static String join(ChessClient client) throws ResponseException {
        // quick check to make sure we actually have some games listed
        if (!listFlag) {
            System.out.println(list(client));
        }

        if (!listFlag) {
            return "Please create a new game of chess";
        }

        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + "Input a game number to join:");
        Scanner scanner = new Scanner(System.in);
        String gameName;
        String teamselect;

        // get the game number from the User
        String input = scanner.nextLine();
        int gameNum;

        try {
            gameNum = Integer.parseInt(input);
        } catch (Error | Exception exception) {
            throw new ResponseException(400, "Must be a listed game number");
        }

        if (gameNum < 1 || gameNum > client.gameList.size()) {
            throw new ResponseException(400, "Must be a listed game number");
        }

        // get the team type from the User
        System.out.println("Select which team to join [W] or [B]");
        teamselect = scanner.nextLine().toLowerCase();

        if (!teamselect.equals("b") && !teamselect.equals("w")) {
            throw new ResponseException(400, "Must be: \"W\" or \"B\"");
        }

        // attempt to actually join the game as one of the teams
        try {
            int gameID = client.gameList.get(gameNum);

            // switch to the team selected
            client.team = switch (teamselect) {
                case "w" -> "WHITE";
                case "b" -> "BLACK";
                default -> throw new ResponseException(400, "Unexpected value: " + teamselect + "\n");
            };

            client.serverFace.join(client.team, gameID, client.authToken);
            client.game = client.gameDataList.get(gameID);
            gameName = client.game.gameName();
            listFlag = false;

            // connect to the WebSocket
            client.webSocketClient = new WebSocketClient(client.serverUrl, client.messageHandler);

            // send the WebSocket data
            client.webSocketClient.joinplayer(client.authToken, client.game.gameID(), client.team);

            client.state = State.GAMEPLAY;
        } catch (Error | ResponseException exception) {
            // if we couldn't log in, make sure the team gets reset
            client.team = "an observer";
            return "Unable to join game: " + exception.getMessage();
        }
        return String.format("You have joined game: %s", gameName);
    }

    public static String observe(ChessClient client) throws ResponseException {
        // quick check to make sure we actually have some games listed
        if (!listFlag) {
            System.out.println(list(client));
        }

        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + "Input a game number to join:");
        Scanner scanner = new Scanner(System.in);
        String gameName;
        String teamselect = null;

        // get the game number from the User
        String input = scanner.nextLine();
        int gameNum;

        try {
            gameNum = Integer.parseInt(input);
        } catch (Error | Exception exception) {
            throw new ResponseException(400, "Must be a listed game number");
        }

        if (gameNum < 1 || gameNum > client.gameList.size()) {
            throw new ResponseException(400, "Must be a listed game number");
        }

        // attempt to actually join the game as one of the teams
        try {
            int gameID = client.gameList.get(gameNum);
            client.team = "an observer";

            client.serverFace.join(null, gameID, client.authToken);
            client.game = client.gameDataList.get(gameID);
            gameName = client.game.gameName();
            listFlag = false;

            // connect to the WebSocket
            client.webSocketClient = new WebSocketClient(client.serverUrl, client.messageHandler);

            // send the WebSocket data
            client.webSocketClient.joinobserver(client.authToken, client.game.gameID());

            client.state = State.GAMEPLAY;
        } catch (ResponseException exception) {
            return "Unable to join game: " + exception.getMessage();
        }
        return String.format("You have joined game: %s", gameName);
    }

    public static String logout(ChessClient client) {
        try {
            client.serverFace.logout(client.authToken);
            client.state = State.LOGGEDOUT;
            listFlag = false;
        } catch (ResponseException exception) {
            return "Unable to logout: " + exception.getMessage();
        }
        return "You have successfully been logged out";
    }
}
