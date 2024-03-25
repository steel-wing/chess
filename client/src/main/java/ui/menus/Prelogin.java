package ui.menus;

import exception.ResponseException;
import result.LoginResponse;
import result.RegisterResponse;
import ui.ChessClient;
import ui.State;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_BOLD;

public class Prelogin {
    public static String register(ChessClient client) throws ResponseException {
        System.out.println(SET_TEXT_COLOR_BLUE + "Please enter your username, password, and email address (space separated):" + RESET);
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] inputs = line.toLowerCase().split(" ");

        if (inputs.length == 3) {
            try {
                RegisterResponse response = client.serverface.register(inputs);

                client.username = response.username();
                client.authToken = response.authToken();
                client.state = State.LOGGEDIN;
            } catch (ResponseException exception) {
                return "Unable to register: " + exception.getMessage();
            }

            return String.format("You are registered and logged in as %s\n", client.username);
        }
        throw new ResponseException(400, "Format: <username> <password> <email>\n");
    }

    public static String login(ChessClient client) throws ResponseException {
        System.out.println(SET_TEXT_COLOR_BLUE + "Please input your username and your password (space separated):" + RESET);
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] inputs = line.toLowerCase().split(" ");

        if (inputs.length == 2) {
            try {
                LoginResponse response = client.serverface.login(inputs);

                client.username = response.username();
                client.authToken = response.authToken();
                client.state = State.LOGGEDIN;
            } catch (ResponseException exception) {
                return "Unable to login: " + exception.getMessage();
            }

            return String.format("You are logged in as %s\n", client.username);
        }
        throw new ResponseException(400, "Format: <username> <password>\n");
    }

    public static String help() {
        return RESET + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + """
        Please select one of the following options:
        [H] : Help for understanding functions and commands
        [L] : Login to your Chess Game account
        [R] : Register a new Chess Game account
        [X] : Exit the Chess Client""";
    }
}
