package chess;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final PieceType type;
    private int steps;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
        this.steps = 0;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN,
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * @return the number of turns the piece has been moved
     */
    public int getSteps() {
        return steps;
    }

    /**
     * increments the number of steps a piece has taken by 1
     */
    public void stepIncrement() {
        steps++;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account meta-moves, like en passant, castling, or moves that endanger the king
     * @return Collection of valid moves
     */
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos){
        return PieceMovesCalculator.pieceMoves(board, pos, type);
    }

    @Override
    public String toString() {
        return color + " " + type;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }
    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
