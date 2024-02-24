package chess.movement;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class KnightMovesCalculator {
    public static ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        // identify as many of the eight vertices that we can jump to
        for (int dir = 2; dir >= -2; dir -= 1) {                // dir is forwards/backwards
            for (int bias = 2; bias >= -2; bias -= 1) {         // bias is right/left
                if (Math.abs(dir) + Math.abs(bias) == 3) {      // forbidden skipping technique
                    moves.addAll(PieceMovesCalculator.linearMotion(pos, dir, bias, 1, board));
                }
            }
        }
        return moves;
    }
}