package chess;

import java.util.Arrays;
import java.util.Map;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;


/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
    }

    /**
     * Copy constructor that makes a deep copy of the board as given
     */
    public ChessBoard(ChessBoard original) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = original.getPiece(new ChessPosition(row, col));
                if (piece != null) {
                    this.addPiece(new ChessPosition(row, col), piece);
                }
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Removes a chess piece from the chessboard
     *
     * @param position where to remove the piece from
     */
    public void removePiece(ChessPosition position) {
        board[position.getRow() - 1][position.getColumn() - 1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        addPiece(new ChessPosition(8, 1), new ChessPiece(BLACK, ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(BLACK, KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(BLACK, BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(BLACK, QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(BLACK, KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(BLACK, BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(BLACK, KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(BLACK, ROOK));
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(7, i), new ChessPiece(BLACK, PAWN));
            addPiece(new ChessPosition(2, i), new ChessPiece(WHITE, PAWN));
        }
        addPiece(new ChessPosition(1, 1), new ChessPiece(WHITE, ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(WHITE, KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(WHITE, BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(WHITE, QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(WHITE, KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(WHITE, BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(WHITE, KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(WHITE, ROOK));

    }

    // stole this from the TestFactory so I could make some nice toString() outputs
    final static Map <ChessPiece.PieceType, Character> TypetoChar = Map.of(
            PAWN, 'p',
            KNIGHT, 'n',
            ROOK, 'r',
            QUEEN, 'q',
            KING, 'k',
            BISHOP, 'b'
    );

    @Override
    // override of string method to make nice box for debugging
    public String toString() {
        StringBuilder output = new StringBuilder();
        // iterate across the board and add all pieces while delimiting with "|"
        for (int row = 8; row >= 1; row--){
            output.append("|");
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = getPiece(new ChessPosition(row, col));
                if (piece == null){
                    output.append(" |");
                    continue;
                }
                // white pieces go uppercase
                Character type = TypetoChar.get(piece.getPieceType());
                type = piece.getTeamColor() == WHITE ? Character.toUpperCase(type) : type;
                output.append(type);
                output.append("|");
            }
            output.append("\n");
        }
        return output.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
