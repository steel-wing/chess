package chess;

import java.util.ArrayList;
import java.util.Map;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

public class ChessGamePrint {

    private static final String UNICODE_ESCAPE = "\u001b";
    private static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";
    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_BG_COLOR_DARK_GREY = SET_BG_COLOR + "237m";
    public static final String RESET_BG_COLOR = "\u001B[0m";

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
    public static String printValids(ChessGame.TeamColor perspective, ChessPosition position, ChessGame game) {
        // make a copy of the board to avoid the evil king travel bug
        ChessBoard board = new ChessBoard(game.getBoard());

        String spacer = "\u2001\u2005\u200A";
        StringBuilder output = new StringBuilder();

        // display selected position if you're asking black
        if (perspective == BLACK) {
            StringBuilder pos = new StringBuilder();
            output.append("\u200A");
            pos.append(position.toFancyString()).reverse();
            output.append(pos);
            output.append("\u2001\u200A\u200A");
        } else {
            output.append("\u2001\u200A  ");
        }

        // display column names
        for (int col = 1; col <= board.cols; col++) {
            output.append((char)(col + '`'));
            output.append(spacer);
        }

        output.append("\u2001\u200A ");

        // Black team condition
        if (game.isInCheck(BLACK)) {
            output.setCharAt(output.length() - 1, '+');
        }
        if (game.isInCheckmate(BLACK)) {
            output.setCharAt(output.length() - 1, '#');
        }

        output.append("\n");

        // iterate across the board and add all pieces while delimiting with "│"
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

        // WHITE team condition
        output.append(" ");
        if (game.isInCheck(WHITE)) {
            output.setCharAt(output.length() - 1, '+');
        }

        output.append("\u2001\u200A ");

        // display column names
        for (int col = 1; col <= board.cols; col++) {
            output.append((char)(col + '`'));
            output.append(spacer);
        }

        output.append("\u200A");

        // display selected position if you're asking white
        if (perspective == WHITE) {
            output.append(position.toFancyString());
        } else {
            output.append("  ");
        }

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
            output.append(colorFlip(type.toString(), row, col));

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
                output.append(colorFlip(spacer, row, col));

                output.append('╣');
            } else {
                // add a piece
                Character type = TypetoGlyph.get(piece.getPieceType());
                type = piece.getTeamColor() == WHITE ? (char) (type + 6) : type;
                output.append(colorFlip(type.toString(), row, col));

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
                output.append(colorFlip(spacer, row, col));
                output.append("│");
            } else {
                // add a piece
                Character type = TypetoGlyph.get(piece.getPieceType());
                type = piece.getTeamColor() == WHITE ? (char) (type + 6) : type;
                output.append(colorFlip(type.toString(), row, col));

                output.append("│");
            }
        }
    }

    public static String gameString(ChessGame game) {
        // make a copy of the board to avoid the evil king travel bug
        ChessBoard board = new ChessBoard(game.getBoard());

        String spacer = "\u2001\u2005\u200A";
        StringBuilder output = new StringBuilder();

        // display if it's Black's turn
        if (game.getTeamTurn() == BLACK) {
            output.append('B');
        } else {
            output.append(' ');
        }

        output.append("\u2001\u200A ");

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

        // iterate across the board and add all pieces while delimiting with "│"
        for (int row = board.rows; row >= 1; row--){
            output.append((char)(row + '0'));
            output.append(" │");
            for (int col = 1; col <= board.cols; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));

                if (piece == null){
                    output.append(colorFlip(spacer, row, col));

                    output.append("│");
                    continue;
                }
                // white pieces are white
                Character type = TypetoGlyph.get(piece.getPieceType());
                type = piece.getTeamColor() == WHITE ? (char)(type + 6) : type;
                output.append(colorFlip(type.toString(), row, col));

                output.append("│");
            }

            // display row names
            output.append("\u2001\u2005");
            output.append((char)(row + '0'));
            output.append("\n");
        }

        // WHITE team condition
        output.append(" ");
        if (game.isInCheck(WHITE)) {
            output.setCharAt(output.length() - 1, '+');
        }
        if (game.isInCheckmate(WHITE)) {
            output.setCharAt(output.length() - 1, '#');
        }

        output.append("\u2001\u200A ");

        // display column names
        for (int col = 1; col <= board.cols; col++) {
            output.append((char)(col + '`'));
            output.append(spacer);
        }

        output.append("\u2001");

        // display if it's White's turn
        if (game.getTeamTurn() == WHITE) {
            output.append('W');
        } else {
            output.append(' ');
        }

        return output.toString();
    }

    /**
     * Helpful little function that handles the swapping of background colors on either side of some inserted string
     * @param input The string to be added to something
     * @param row The row of context
     * @param col The col of context
     * @return The correctly background-flipped strin
     */
    private static String colorFlip(String input, int row, int col) {
        StringBuilder output = new StringBuilder();

        // change the color if it should be changed
        if ((row + col) % 2 == 0) {
            output.append(SET_BG_COLOR_BLACK);
        } else {
            output.append(SET_BG_COLOR_DARK_GREY);
        }

        output.append(input);

        // change it back, adding in color tags for later flipping
        if ((row + col) % 2 == 0) {
            output.append(SET_BG_COLOR_BLACK + RESET_BG_COLOR);
        } else {
            output.append(SET_BG_COLOR_DARK_GREY + RESET_BG_COLOR);
        }

        return output.toString();
    }

    /**
     * A simple little function for rotating a gameboard
     *
     * @param inputGame an input game (from White's perspective
     * @return The string from before but rotated 180 degrees for the other player
     */
    public static String gameFlip(String inputGame) {
        String output = new StringBuilder(inputGame).reverse().toString();

        // replace the directions of directionals
        output = output.replaceAll("╣", "y");
        output = output.replaceAll("╠", "╣");
        output = output.replaceAll("y", "╠");

        // replace the reversed color commands
        output = output.replaceAll("m0\\[\\u001Bm732;5;84\\[\\u001B", "\u001B[48;5;237m");  // reset -> grey
        output = output.replaceAll("m0\\[\\u001Bm0;5;84\\[\\u001B", "\u001B[48;5;0m");      // reset -> black

        output = output.replaceAll("m732;5;84\\[\\u001B", "\u001B[0m");                     // grey -> reset
        output = output.replaceAll("m0;5;84\\[\\u001B", "\u001B[0m");                       // black -> reset

        return output;
    }
}
