package ui.menus;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import ui.ChessClient;
import ui.State;

import java.util.Scanner;

import static chess.ChessPiece.PieceType.*;
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
        System.out.println(client.game.game());
        return RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_WHITE + client.game.game().toString(team);
    }

    public static String makeMove(ChessClient client) throws ResponseException {
        // handle all of the end cases
        if(client.game.game().isInCheckmate(ChessGame.TeamColor.WHITE) ||
            client.game.game().isInCheckmate(ChessGame.TeamColor.BLACK) ||
            client.game.game().isInStalemate(ChessGame.TeamColor.WHITE) ||
            client.game.game().isInStalemate(ChessGame.TeamColor.BLACK) ||
            client.game.game().getWinner() != null) {
            return "Cannot move, the game is over";
        }

        if (client.team.equals("an observer") || client.game.game().getTeamTurn() != team(client)) {
            throw new ResponseException(400, "It is not your turn");
        }

        // ask for starting location
        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + "Make your move: <start> <end>");
        Scanner scanner = new Scanner(System.in);

        // get the starting position
        String line = scanner.nextLine();
        String[] inputs = line.toLowerCase().split(" ");

        if (inputs.length != 2) {
            throw new ResponseException(400, "Chess Notation: \"A1\" to \"H8\"");
        }

        ChessPosition start = new ChessPosition(inputs[0]);
        ChessPosition end = new ChessPosition(inputs[1]);

        if (start.getRow() > 8 || start.getRow() < 1 || start.getColumn() > 8 || start.getColumn() < 1) {
            throw new ResponseException(400, "Chess Notation: \"A1\" or \"H8\"");
        }

        ChessGame.TeamColor team = team(client);
        if (client.game.game().getBoard().getPiece(start).getTeamColor() != team) {
            throw new ResponseException(400, "Start position is not possessed by your team");
        }

        // get piece to be moved
        ChessPiece piece = client.game.game().getBoard().getPiece(start);

        // verify that the end position is legal
        if (end.getRow() > 8 || end.getRow() < 1 || end.getColumn() > 8 || end.getColumn() < 1) {
            throw new ResponseException(400, "Chess Notation: \"A1\" or \"H8\"");
        }

        if (!client.game.game().validMoves(start).contains(new ChessMove(start, end, null))) {
            throw new ResponseException(400, "Invalid move requested");
        }

        // handle pawn promotion
        ChessPiece.PieceType promo = null;
        if (piece.getPieceType() == PAWN && (end.getRow() == 1 || end.getRow() == 8)) {
            // ask what they want it to be
            System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + "Pawn Promotion: [R] [N] [B] [Q] ?");
            String input = scanner.nextLine().toLowerCase();
            promo = switch (input) {
                case "r" -> ROOK;
                case "n" -> KNIGHT;
                case "b" -> BISHOP;
                case "q" -> QUEEN;
                default -> throw new ResponseException(400, "Not an option");
            };
        }

        // make the move
        ChessMove move = new ChessMove(start, end, promo);

        // update everyone else about the move, include details
        client.webSocketClient.makemove(client.authToken, client.game.gameID(), move);

        return "You have made move: " + move;
    }

    public static String validMoves(ChessClient client) throws ResponseException{
        // handle all of the end cases
        if(client.game.game().isInCheckmate(ChessGame.TeamColor.WHITE) ||
                client.game.game().isInCheckmate(ChessGame.TeamColor.BLACK) ||
                client.game.game().isInStalemate(ChessGame.TeamColor.WHITE) ||
                client.game.game().isInStalemate(ChessGame.TeamColor.BLACK) ||
                client.game.game().getWinner() != null) {
            return "No available moves, the game is over";
        }

        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + "Select a square that you would like to observe:");
        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();
        ChessPosition position = new ChessPosition(input);

        if (position.getRow() > 8 || position.getRow() < 1 || position.getColumn() > 8 || position.getColumn() < 1) {
            throw new ResponseException(400, "Chess Notation: \"A1\" or \"H8\"");
        }

        ChessGame.TeamColor team = team(client);
        if (client.game.game().getBoard().getPiece(position) == null ||
            client.game.game().getBoard().getPiece(position).getTeamColor() != team) {
            throw new ResponseException(400, "Position is not possessed by your team");
        }

        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + "Valid Moves:");
        return RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_WHITE + client.game.game().printValids(team, position) ;
    }

    public static String resign(ChessClient client) throws ResponseException {
        // handle all of the end cases
        if(client.game.game().isInCheckmate(ChessGame.TeamColor.WHITE) ||
                client.game.game().isInCheckmate(ChessGame.TeamColor.BLACK) ||
                client.game.game().isInStalemate(ChessGame.TeamColor.WHITE) ||
                client.game.game().isInStalemate(ChessGame.TeamColor.BLACK) ||
                client.game.game().getWinner() != null) {
            return "Cannot resign, the game is over";
        }
        // we have to make the game be won by the other team, and lost by the person who called this
        client.webSocketClient.resign(client.authToken, client.game.gameID());
        return "You have resigned from the game";
    }

    public static String exit(ChessClient client) throws ResponseException {
        client.webSocketClient.leave(client.authToken, client.game.gameID());
        client.game = null;
        client.team = "as observer";
        client.state = State.LOGGEDIN;
        return "You have successfully exited the game";
    }

    private static ChessGame.TeamColor team(ChessClient client) {
        // get the team color
        ChessGame.TeamColor team;
        if (client.team.equals("BLACK")) {
            team = ChessGame.TeamColor.BLACK;
        } else {
            team = ChessGame.TeamColor.WHITE;
        }
        return team;
    }
}
