package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        // move in all eight directions
        for (int dir = 1; dir >= -1; dir -= 1) {                // dir is forwards/backwards
            for (int bias = 1; bias >= -1; bias -= 1) {         // bias is right/left
                if (dir != 0 || bias != 0) {                    // skip targeting ourselves
                    moves.addAll(ChessPiece.linearMotion(pos, dir, bias, 8, board));
                }
            }
        }
        return moves;
    }
}