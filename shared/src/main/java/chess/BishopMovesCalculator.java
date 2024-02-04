package chess;

import java.util.ArrayList;

public class BishopMovesCalculator {
    public static ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        // only move along the diagonals
        for (int dir = 1; dir >= -1; dir -= 2) {                 // dir is forwards/backwards
            for (int bias = 1; bias >= -1; bias -= 2) {          // bias is right/left
                moves.addAll(ChessPiece.linearMotion(pos, dir, bias, 8, board));
            }
        }
        return moves;
    }
}