package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        // only move up, down, left, and right
        for (int dir = 1; dir >= -1; dir -= 1) {                 // dir is forwards/backwards
            for (int bias = 1; bias >= -1; bias -= 1) {          // bias is right/left
                // forbidden skipping technique to minimize necessary code revisions don't @ me
                if (dir * bias != 0){
                    continue;
                }
            moves.addAll(PieceMovesCalculator.linearMotion(myPosition, dir, bias, board));
            }
        }
        return moves;
    }
}