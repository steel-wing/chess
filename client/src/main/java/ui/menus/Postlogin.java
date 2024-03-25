package ui.menus;

import exception.ResponseException;
import model.GameData;
import result.CreateResponse;
import ui.ChessClient;
import ui.State;

import java.util.ArrayList;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Postlogin {
    public static String logout(ChessClient client) {
        try {
            client.serverface.logout(client.authToken);
            client.state = State.LOGGEDOUT;
        } catch (ResponseException exception) {
            return "Unable to logout: " + exception.getMessage();
        }
        return "You have successfully been logged out";
    }

    public static String create(ChessClient client) throws ResponseException {
        System.out.println(SET_TEXT_COLOR_BLUE + "Please choose a name for your game:" + RESET);
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] input = line.toLowerCase().split(" ");

        if (input.length == 1) {
            try {
                CreateResponse response = client.serverface.create(input[0], client.authToken);
                client.gamename = input[0];
            } catch (ResponseException exception) {
                return "Unable to create game: " + exception.getMessage();
            }
            return String.format("You have created game: %s", client.gamename);
        }
        throw new ResponseException(400, "Format: <gamename>");
    }

    public static String list(ChessClient client) throws ResponseException {
        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + "Games List:");

        try {
            // retrieve the list of games, store it, and print it out
            ArrayList<GameData> list = client.serverface.list(client.authToken).games();
            StringBuilder output = new StringBuilder();
            output.append(RESET_TEXT_BOLD_FAINT);

            for (int i = 1; i <= list.size(); i++) {
                GameData game = list.get(i - 1);
                client.gamelist.put(i - 1, game.gameID());
                // "Game 4: boogerAIDS (W: RickSanchez, B: MortySmith)"
                String linestring = "Game " + i + ": " + game.gameName() + " (W: " + game.whiteUsername() + ", B: " + game.blackUsername() + ")\n";
                output.append(linestring);
            }

            // forbidden newline deletion technique
            output.delete(output.length() - 2, output.length());

            return output.toString();
        } catch (ResponseException exception) {
            return "Unable to access list: " + exception.getMessage();
        }
    }

    public static String help(ChessClient client) {
        return RESET + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + """
        Please select one of the following options:
        [H] : Help for understanding functions and commands
        [L] : List the current running chess games
        [C] : Create a new game of chess
        [J] : Join an existing game of chess
        [W] : Watch an existing game of chess
        [X] : Logout and return to the Chess Game Client""";
    }
}
