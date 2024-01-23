package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        // only move along the diagonals
        for (int dir = 1; dir >= -1; dir -= 2) {                 // dir is forwards/backwards
            for (int bias = 1; bias >= -1; bias -= 2) {          // bias is right/left
                moves.addAll(PieceMovesCalculator.linearMotion(pos, dir, bias, 8, board));
            }
        }
        return moves;
    }
}