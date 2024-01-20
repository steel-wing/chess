package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        // move in all eight directions
        for (int dir = 1; dir >= -1; dir -= 1) {                 // dir is forwards/backwards
            for (int bias = 1; bias >= -1; bias -= 1) {          // bias is right/left
                ChessGame.TeamColor team = board.getPiece(pos).getTeamColor();

                int row = pos.getRow();
                int col = pos.getColumn();

                int targetrow = row + dir;
                int targetcol = col + bias;

                // while in the bounds of the board, and within only one step of the king
                while (targetrow >= 1 && targetrow <= 8 && targetcol >= 1 && targetcol <= 8
                        && targetrow >= row - 1 && targetrow <= row + 1
                        && targetcol >= col - 1 && targetcol <= col + 1) {

                    // identify the target square
                    ChessPiece target = board.getPiece(new ChessPosition(targetrow, targetcol));

                    // if we hit an empty space, add it it to the available moves
                    if (target == null) {
                        moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), null));
                    } else {
                        // if we hit a piece, check to see if its an enemy. If so, we can hit it, but no further
                        if (target.getTeamColor() != team) {
                            moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), null));
                        }
                        break;
                    }
                    targetrow += dir;
                    targetcol += bias;
                }
            }
        }
        return moves;
    }
}