package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team = board.getPiece(myPosition).getTeamColor();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int targetrow;
        int targetcol;

        // identify as many of the eight vertices that we can jump to
        for (int dir = 2; dir >= -2; dir -= 1) {                 // dir is forwards/backwards
            for (int bias = 2; bias >= -2; bias -= 1) {          // bias is right/left
                // forbidden skipping technique to minimize necessary code revisions don't @ me
                if (Math.abs(dir) + Math.abs(bias) != 3) {
                    continue;
                }

                // identify landing spot and skip those that are out of bounds
                targetrow = row + dir;
                targetcol = col + bias;
                if (targetrow < 1 || targetrow > 8 || targetcol < 1 || targetcol > 8) {
                    continue;
                }

                // identify the target square
                ChessPiece target = board.getPiece(new ChessPosition(targetrow, targetcol));

                // if we hit an empty space, add it it to the available moves
                if (target == null) {
                    moves.add(new chess.ChessMove(myPosition, new ChessPosition(targetrow, targetcol), null));
                } else {
                    // if we hit a piece, check to see if its an enemy. If so, we can hit it
                    if (target.getTeamColor() != team) {
                        moves.add(new chess.ChessMove(myPosition, new ChessPosition(targetrow, targetcol), null));
                    }
                }
            }
        }
        return moves;
    }
}

