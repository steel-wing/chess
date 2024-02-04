package chess;

import java.util.ArrayList;

public class RookMovesCalculator {
    public static ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        // only move up, down, left, and right
        for (int dir = 1; dir >= -1; dir -= 1) {                // dir is forwards/backwards
            for (int bias = 1; bias >= -1; bias -= 1) {         // bias is right/left
                if (dir == 0 ^ bias == 0){                      // skip diagonals and targeting ourselves
                    moves.addAll(ChessPiece.linearMotion(pos, dir, bias, 8, board));
                }
            }
        }
        return moves;
    }
}