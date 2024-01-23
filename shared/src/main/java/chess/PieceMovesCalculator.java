package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    /**
     * Delegates calculations of all the positions a specific chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos, ChessPiece.PieceType type){
        if (type == ChessPiece.PieceType.PAWN){
            return PawnMovesCalculator.pieceMoves(board, pos);
        } else if (type == ChessPiece.PieceType.KNIGHT){
            return KnightMovesCalculator.pieceMoves(board, pos);
        } else if (type == ChessPiece.PieceType.BISHOP){
            return BishopMovesCalculator.pieceMoves(board, pos);
        } else if (type == ChessPiece.PieceType.ROOK){
            return RookMovesCalculator.pieceMoves(board, pos);
        } else if (type == ChessPiece.PieceType.QUEEN){
            return QueenMovesCalculator.pieceMoves(board, pos);
        } else if (type == ChessPiece.PieceType.KING){
            return KingMovesCalculator.pieceMoves(board, pos);
        }
        // this is just here since Java yells at me if it isn't here
        return null;
    }

    // god-tier iteration technique for all eight linear directions
    public static ArrayList<ChessMove> linearMotion(ChessPosition pos, int dir, int bias, int steps, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team = board.getPiece(pos).getTeamColor();

        int row = pos.getRow();
        int col = pos.getColumn();

        int targetrow = row + dir;
        int targetcol = col + bias;

        // while in the bounds of the board, and not overstepping
        while (targetrow >= 1 && targetrow <= 8 && targetcol >= 1 && targetcol <= 8 && steps > 0) {

            // identify the target square
            ChessPiece target = board.getPiece(new ChessPosition(targetrow, targetcol));

            // if we hit an empty space, add it to the available moves
            if (target == null) {
                moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), null));

                // iterate onwards in the direction indicated by dir and bias
                targetrow += dir;
                targetcol += bias;
                steps--;
            } else {
                // if we hit a piece we go no further. Check to see if it's an enemy: if so, we can hit it
                if (target.getTeamColor() != team) {
                    moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), null));
                }
                break;
            }
        }
        return moves;
    }
}