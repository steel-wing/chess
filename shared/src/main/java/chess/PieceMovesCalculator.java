package chess;

import java.util.Collection;

public class PieceMovesCalculator {
    /**
     * Delegates calculations of all the positions a specific chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece.PieceType type){
        if (type == ChessPiece.PieceType.PAWN){
            return PawnMovesCalculator.pieceMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.KNIGHT){
            return KnightMovesCalculator.pieceMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.BISHOP){
            return BishopMovesCalculator.pieceMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.ROOK){
            return RookMovesCalculator.pieceMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.QUEEN){
            return QueenMovesCalculator.pieceMoves(board, myPosition);
        } else if (type == ChessPiece.PieceType.KING){
            return KingMovesCalculator.pieceMoves(board, myPosition);
        }
        // this is just here since Java yells at me if it isn't here
        return null;
    }
}
