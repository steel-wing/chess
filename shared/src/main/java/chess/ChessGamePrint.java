package chess;

import java.util.ArrayList;
import java.util.Map;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;
import static chess.ChessPiece.PieceType.BISHOP;

public class ChessGamePrint {
    final static Map<ChessPiece.PieceType, Character> TypetoGlyph = Map.of(
            PAWN, '♙',
            KNIGHT, '♘',
            ROOK, '♖',
            QUEEN, '♕',
            KING, '♔',
            BISHOP, '♗'
    );

    /**
     * Prints a chessboard nice and pretty-like, with all valid moves from a position displayed
     *
     * @param position The position from which to look for valid moves
     * @return A really pretty chessboard interpretation
     */
    public static String printValids(ChessPosition position, ChessGame game) {
        ChessBoard board = game.getBoard();
        String spacer = "\u2001\u2005\u200A";
        StringBuilder output = new StringBuilder();

        // display selected position
        output.append(position.toFancyString());
        output.append("\u2001\u200A");

        // display column names
        for (int col = 1; col <= board.cols; col++) {
            output.append((char)(col + '`'));
            output.append(spacer);
        }

        // Black team condition
        output.append("\u2001\u200A ");
        if (game.isInCheck(BLACK)) {
            output.setCharAt(output.length() - 1, '+');
        }
        output.append("\n");

        // iterate across the board and add all pieces while delimiting with "║"
        for (int row = board.rows; row >= 1; row--){
            output.append((char)(row + '0'));
            output.append(" │");
            for (int col = 1; col <= board.cols; col++) {
                // draw the next square, applying good logic
                addSquare(position, output, row, col, game);
            }

            // display row names
            output.append("\u2001\u2005");
            output.append((char)(row + '0'));
            output.append("\n");
        }

        // display who's turn it is
        if (game.getTeamTurn() == WHITE) {
            output.append('W');
        } else {
            output.append('B');
        }
        output.append(" \u2001\u200A");

        // display column names
        for (int col = 1; col <= board.cols; col++) {
            output.append((char)(col + '`'));
            output.append(spacer);
        }

        // WHITE team condition
        output.append("\u2001\u200A ");
        if (game.isInCheck(WHITE)) {
            output.setCharAt(output.length() - 1, '+');
        }
        output.append("\n");

        return output.toString();
    }

    /**
     * A helper function for printValids() which builds a pretty square based on context
     */
    static void addSquare(ChessPosition position, StringBuilder output, int row, int col, ChessGame game) {
        ChessBoard board = game.getBoard();

        String spacer = "\u2001\u2005\u200A";
        ChessPosition select = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(select);
        boolean lastflag = false;

        // build array list of positions, not moves
        ArrayList<ChessMove> validmoves = game.validMoves(position);
        ArrayList<ChessPosition> valids = new ArrayList<>();
        for (ChessMove move : validmoves) {
            valids.add(move.getEndPosition());
        }

        // look to the previous square if it is targeted
        if (col > 1) {
            ChessPosition lastselect = new ChessPosition(row, col - 1);
            ChessPiece lastpiece = board.getPiece(select);
            lastflag = valids.contains(lastselect);
        }

        // check to see if we're on ourself
        if (position.getRow() == row && position.getColumn() == col) {
            // add a piece
            if (output.charAt(output.length() - 1) == '│') {
                output.setCharAt(output.length() - 1, '║');
            }
            Character type = TypetoGlyph.get(piece.getPieceType());
            type = piece.getTeamColor() == WHITE ? (char) (type + 6) : type;
            output.append(type);
            output.append("║");
            return;
        }

        // looking at our current square
        if (valids.contains(select)) {
            // if the last square was selected, update the selector
            if (lastflag) {
                output.setCharAt(output.length() - 1, '╬');
            } else {
                output.setCharAt(output.length() - 1, '╠');
            }
            // finish the selector
            if (piece == null){
                // add an empty space
                output.append(spacer);
                output.append('╣');
            } else {
                // add a piece
                Character type = TypetoGlyph.get(piece.getPieceType());
                type = piece.getTeamColor() == WHITE ? (char) (type + 6) : type;
                output.append(type);
                output.append("╣");
            }
        } else {
            if (lastflag) {
                output.setCharAt(output.length() - 1, '╣');
            } else {
                if (output.charAt(output.length() - 1) != '║') {
                    output.setCharAt(output.length() - 1, '│');
                }
            }
            if (piece == null){
                // add an empty space
                output.append(spacer);
                output.append("│");
            } else {
                // add a piece
                Character type = TypetoGlyph.get(piece.getPieceType());
                type = piece.getTeamColor() == WHITE ? (char) (type + 6) : type;
                output.append(type);
                output.append("│");
            }
        }
    }

    /**
     * A simple little function for rotating a gameboard
     *
     * @param inputGame an input game (from White's perspective
     * @return The string from before but rotated 180 degrees for the other player
     */
    public static String gameFlip(String inputGame) {
        //System.out.println(game.gameFlip(game.toString()));
        //System.out.println(game.gameFlip(game.printValids()));
        StringBuilder output = new StringBuilder();
        output.append(inputGame);
        output.reverse();
        return output.toString();
    }

    public static String gameString(ChessGame game) {
        ChessBoard board = game.getBoard();
        String spacer = "\u2001\u2005\u200A";

        StringBuilder output = new StringBuilder();
        output.append(spacer);
        output.append(spacer);
        // display column names
        for (int col = 1; col <= board.cols; col++) {
            output.append((char)(col + '`'));
            output.append(spacer);
        }

        // Black team condition
        output.append("\u2001\u200A ");
        if (game.isInCheck(BLACK)) {
            output.setCharAt(output.length() - 1, '+');
        }
        if (game.isInCheckmate(BLACK)) {
            output.setCharAt(output.length() - 1, '#');
        }
        output.append("\n");

        // iterate across the board and add all pieces while delimiting with "║"
        for (int row = board.rows; row >= 1; row--){
            output.append((char)(row + '0'));
            output.append(" │");
            for (int col = 1; col <= board.cols; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece == null){
                    output.append(spacer);
                    output.append("│");
                    continue;
                }
                // white pieces are white
                Character type = TypetoGlyph.get(piece.getPieceType());
                type = piece.getTeamColor() == WHITE ? (char)(type + 6) : type;
                output.append(type);
                output.append("│");
            }

            // display row names
            output.append("\u2001\u2005");
            output.append((char)(row + '0'));
            output.append("\n");
        }

        // display who's turn it is
        if (game.getTeamTurn() == WHITE) {
            output.append('W');
        } else {
            output.append('B');
        }
        output.append(" \u2001\u200A");

        // display column names
        for (int col = 1; col <= board.cols; col++) {
            output.append((char)(col + '`'));
            output.append(spacer);
        }

        // WHITE team condition
        output.append("\u2001\u200A ");
        if (game.isInCheck(WHITE)) {
            output.setCharAt(output.length() - 1, '+');
        }
        if (game.isInCheckmate(WHITE)) {
            output.setCharAt(output.length() - 1, '#');
        }
        output.append("\n");

        return output.toString();
    }
}
