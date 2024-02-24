package chess.movement;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator {
    public static ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team = board.getPiece(pos).getTeamColor();

        int dir = team == ChessGame.TeamColor.WHITE ? 1 : -1; // white pawn goes up, black goes down

        int row = pos.getRow();
        int col = pos.getColumn();

        int targetrow = row + dir;
        int targetcol = col;

        ChessPiece target = board.getPiece(new ChessPosition(targetrow, targetcol));

        // check if the square in front of us is clear
        if (target == null) {
            moves.addAll(pawnMotion(pos, targetrow, targetcol));

            // check if we're in the front row to do the double skip
            if (row == 4.5 - 2.5 * dir) {
                targetrow += dir;
                target = board.getPiece(new ChessPosition(targetrow, targetcol));
                if (target == null) {
                    moves.addAll(pawnMotion(pos, targetrow, targetcol));
                }
                targetrow -= dir;
            }
        }

        // check the right flank
        if (col <= 7) {
            targetcol += 1;
            target = board.getPiece(new ChessPosition(targetrow, targetcol));
            if (target != null && team != target.getTeamColor()) {
                moves.addAll(pawnMotion(pos, targetrow, targetcol));
            }
            targetcol -= 1;     // step back down
        }

        // check the left flank
        if (col >= 2) {
            targetcol -= 1;
            target = board.getPiece(new ChessPosition(targetrow, targetcol));
            if (target != null && team != target.getTeamColor()) {
                moves.addAll(pawnMotion(pos, targetrow, targetcol));
            }
        }

        return moves;
    }

    /**
     * Helper function for handling pawn promotion cases
     * Assumes that all other placement checks have been made prior
     */
    private static Collection<ChessMove> pawnMotion(ChessPosition pos, int targetrow, int targetcol) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        // if we can promote, offer all options
        if (targetrow == 1 || targetrow == 8) {
            moves.add(new ChessMove(pos, new ChessPosition(targetrow, targetcol), ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(pos, new ChessPosition(targetrow, targetcol), ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(pos, new ChessPosition(targetrow, targetcol), ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(pos, new ChessPosition(targetrow, targetcol), ChessPiece.PieceType.QUEEN));
        } else {
            // if not, just move
            moves.add(new ChessMove(pos, new ChessPosition(targetrow, targetcol), null));
        }
        return moves;
    }
}