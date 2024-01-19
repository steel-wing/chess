package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PieceMovesCalculator {
    public PieceMovesCalculator() {
    }

    final static Map<ChessPiece.PieceType, Character> TypetocharMap = Map.of(
            ChessPiece.PieceType.PAWN, 'p',
            ChessPiece.PieceType.KNIGHT, 'n',
            ChessPiece.PieceType.ROOK, 'r',
            ChessPiece.PieceType.QUEEN, 'q',
            ChessPiece.PieceType.KING, 'k',
            ChessPiece.PieceType.BISHOP, 'b'
    );

    /**
     * Delegates calculations of all the positions a specific chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece.PieceType type){

        return new ArrayList<>();
    }


}
