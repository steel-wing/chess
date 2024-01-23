package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team = board.getPiece(pos).getTeamColor();

        int row = pos.getRow();
        int col = pos.getColumn();

        int targetrow = row;
        int targetcol = col;

        int dir = team == ChessGame.TeamColor.WHITE ? 1 : -1; // white pawn goes up, black goes down

        // while in the middle 6 rows
        if (row >= 2 && row <= 7) {
            targetrow += dir;
            // check square directly in front: only move if it's empty
            if (board.getPiece(new ChessPosition(targetrow, targetcol)) == null) {
                moves.addAll(pawnMotion(pos, targetrow, targetcol));

                // check square two in front if we're in our home row
                if (row == (4.5 - 2.5 * dir)) {     // clever lil math to see if we're on our own front lines
                    targetrow += dir;
                    if (board.getPiece(new ChessPosition(targetrow, targetcol)) == null) {
                        moves.addAll(pawnMotion(pos, targetrow, targetcol));
                        // back back out
                        targetrow -= dir;
                    }
                }
            }

            // check the right flank
            if (col + 1 <= 8) {
                targetcol = col + 1;
                ChessPiece target = board.getPiece(new ChessPosition(targetrow, targetcol));
                if (target != null && target.getTeamColor() != team) {
                    moves.addAll(pawnMotion(pos, targetrow, targetcol));
                }
            }

            // check the left flank
            if (col - 1 >= 1) {
                targetcol = col - 1;
                ChessPiece target = board.getPiece(new ChessPosition(targetrow, targetcol));
                if (target != null && target.getTeamColor() != team) {
                    moves.addAll(pawnMotion(pos, targetrow, targetcol));
                }
            }
        }
        return moves;
    }

    // helper function for handling promotion cases
    private static Collection<ChessMove> pawnMotion(ChessPosition pos, int targetrow, int targetcol) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        // if we can promote, offer all options
        if (targetrow == 1 || targetrow == 8) {
            moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), ChessPiece.PieceType.KNIGHT));
            moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), ChessPiece.PieceType.BISHOP));
            moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), ChessPiece.PieceType.ROOK));
            moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), ChessPiece.PieceType.QUEEN));
        } else {
            // if not, just move
            moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), null));
        }
        return moves;
    }
}