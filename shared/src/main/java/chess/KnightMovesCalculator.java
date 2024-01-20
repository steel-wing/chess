package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team = board.getPiece(pos).getTeamColor();

        int row = pos.getRow();
        int col = pos.getColumn();

        // identify as many of the eight vertices that we can jump to
        for (int dir = 2; dir >= -2; dir -= 1) {                 // dir is forwards/backwards
            for (int bias = 2; bias >= -2; bias -= 1) {          // bias is right/left
                // forbidden skipping technique
                if (Math.abs(dir) + Math.abs(bias) != 3) {
                    continue;
                }

                // identify landing spot and skip those that are out of bounds
                int targetrow = row + dir;
                int targetcol = col + bias;
                if (targetrow < 1 || targetrow > 8 || targetcol < 1 || targetcol > 8) {
                    continue;
                }

                // identify the target square
                ChessPiece target = board.getPiece(new ChessPosition(targetrow, targetcol));

                // if we hit an empty space or an enemy
                if (target == null || target.getTeamColor() != team) {
                    moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), null));
                }
            }
        }
        return moves;
    }
}