package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team = board.getPiece(pos).getTeamColor();

        int row = pos.getRow();
        int col = pos.getColumn();

        // move one step in all eight directions
        for (int dir = 1; dir >= -1; dir -= 1) {                // dir is forwards/backwards
            for (int bias = 1; bias >= -1; bias -= 1) {         // bias is right/left
                if (dir != 0 || bias != 0) {                    // skip targeting ourselves
                    int targetrow = row + dir;
                    int targetcol = col + bias;

                    // while in the bounds of the board
                    if (targetrow >= 1 && targetrow <= 8 && targetcol >= 1 && targetcol <= 8) {
                        // identify the target square
                        ChessPiece target = board.getPiece(new ChessPosition(targetrow, targetcol));

                        // if we hit an empty space or an enemy
                        if (target == null || target.getTeamColor() != team) {
                            moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), null));
                        }
                    }
                }
            }
        }
        return moves;
    }
}