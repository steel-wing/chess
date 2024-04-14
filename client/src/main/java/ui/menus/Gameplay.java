package ui.menus;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import ui.ChessClient;
import ui.State;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Gameplay {

    public static String help() {
        return RESET + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + """
        Please select one of the following options:
        [H] : Help for understanding functions and commands
        [P] : Print the current game of Chess
        [M] : Make a move
        [S] : Show the legal moves available to a certain piece
        [R] : Resign from the game
        [X] : Exit and return to the Chess Game Menu""";
    }

    public static String redraw(ChessClient client) {
        ChessGame.TeamColor team = team(client);

        return RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_WHITE + client.game.game().toString(team) + "\n" +
               client.game.game().toString(ChessGame.TeamColor.BLACK);
    }

    public static String makeMove(ChessClient client) throws ResponseException {
        // ask for starting location

        ChessPosition start = new ChessPosition(1, 1);

        // throw error if cannot move

        // get piece to be moved

        // ask where they want to put it

        ChessPosition end = new ChessPosition(1, 1);

        // if pawn promotion, ask what they want it to be

        ChessPiece.PieceType promo = ChessPiece.PieceType.QUEEN;

        // make the move
        ChessMove move = new ChessMove(start, end, promo);

        // update everyone else about the move
        client.webSocketClient.makemove(client.authToken, client.game.gameID(), move);

        return "You have made move: " + move;
    }

    public static String validMoves(ChessClient client) throws ResponseException{
        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + "Select a square that you would like to observe:");
        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();
        ChessPosition position = new ChessPosition(input);

        if (position.getRow() > 8 || position.getRow() < 1 || position.getColumn() > 8 || position.getColumn() < 1) {
            throw new ResponseException(400, "Chess Notation: \"A1\" or \"H8\"");
        }

        ChessGame.TeamColor team = team(client);
        if (client.game.game().getBoard().getPiece(position).getTeamColor() != team) {
            throw new ResponseException(400, "Position is not possessed by your team");
        }

        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + "Valid Moves:");
        return client.game.game().printValids(team, position);
    }

    public static String resign(ChessClient client) {
        // yeah we'll put stuff here soon

        return "You have resigned from the game";
    }

    public static String exit(ChessClient client) {
        client.game = null;
        client.team = "as observer";
        client.state = State.LOGGEDIN;
        return "You have successfully exited the game";
    }

    private static ChessGame.TeamColor team(ChessClient client) {
        // get the team color
        ChessGame.TeamColor team;
        if (client.team.equals("Black")) {
            team = ChessGame.TeamColor.BLACK;
        } else {
            team = ChessGame.TeamColor.WHITE;
        }
        return team;
    }
}
