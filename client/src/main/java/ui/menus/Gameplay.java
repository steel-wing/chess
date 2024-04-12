package ui.menus;

import chess.ChessGame;
import model.GameData;
import ui.ChessClient;
import ui.State;

import static ui.EscapeSequences.*;

public class Gameplay {

    public static String help() {
        return RESET + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + """
        Please select one of the following options:
        [H] : Help for understanding functions and commands
        [R] : Redraw the current game of Chess
        [M] : Make a move
        [X] : Exit and return to the Chess Game Menu""";
    }

    public static String redraw(ChessClient client) {
        // upload a chessgame to the gameData saved in the client
        GameData old = client.game;
        client.game = new GameData(old.gameID(), old.whiteUsername(), old.blackUsername(), old.gameName(), new ChessGame());

        return RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_WHITE + client.game.game().toString(ChessGame.TeamColor.WHITE) + "\n" +
               client.game.game().toString(ChessGame.TeamColor.BLACK);
    }

    public static String exit(ChessClient client) {
        client.game = null;
        client.team = "as observer";
        client.state = State.LOGGEDIN;
        return "You have successfully exited the game";
    }
}
