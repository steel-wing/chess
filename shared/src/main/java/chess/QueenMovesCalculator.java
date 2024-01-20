package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        // move in all eight directions
        for (int dir = 1; dir >= -1; dir -= 1) {                 // dir is forwards/backwards
            for (int bias = 1; bias >= -1; bias -= 1) {          // bias is right/left
                moves.addAll(PieceMovesCalculator.linearMotion(myPosition, dir, bias, board));
            }
        }
        return moves;
    }
}