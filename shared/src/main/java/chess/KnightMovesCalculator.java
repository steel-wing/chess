package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        // identify as many of the eight vertices that we can and could jump to
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int targetrow;
        int targetcol;

//        // move to all eight spots
//        for (int dir = 1; dir >= -1; dir -= 1) {                 // dir is forwards/backwards
//            for (int bias = 1; bias >= -1; bias -= 1) {          // bias is right/left
//
//        int targetrow = row + dir;
//        int targetcol = col + bias;

        if (row + 2 <= 8) {
            targetrow = row + 2;
            if (col + 1 <= 8) {
                targetcol = col + 1;
                ChessMove move = knightLanding(board, targetrow, targetcol, myPosition);
                if (move != null){
                    moves.add(move);
                }
            }
            if (col - 1 >= 1) {
                targetcol = col - 1;
                ChessMove move = knightLanding(board, targetrow, targetcol, myPosition);
                if (move != null){
                    moves.add(move);
                }
            }
        }
        if (col + 2 <= 8) {
            targetcol = col + 2;
            if (row + 1 <= 8) {
                targetrow = row + 1;
                ChessMove move = knightLanding(board, targetrow, targetcol, myPosition);
                if (move != null){
                    moves.add(move);
                }
            }
            if (row - 1 >= 1) {
                targetrow = row - 1;
                ChessMove move = knightLanding(board, targetrow, targetcol, myPosition);
                if (move != null){
                    moves.add(move);
                }
            }
        }
        if (row - 2 >= 1) {
            targetrow = row - 2;
            if (col + 1 <= 8) {
                targetcol = col + 1;
                ChessMove move = knightLanding(board, targetrow, targetcol, myPosition);
                if (move != null){
                    moves.add(move);
                }
            }
            if (col - 1 >= 1) {
                targetcol = col - 1;
                ChessMove move = knightLanding(board, targetrow, targetcol, myPosition);
                if (move != null){
                    moves.add(move);
                }
            }
        }
        if (col - 2 >= 1) {
            targetcol = col - 2;
            if (row + 1 <= 8) {
                targetrow = row + 1;
                ChessMove move = knightLanding(board, targetrow, targetcol, myPosition);
                if (move != null){
                    moves.add(move);
                }
            }
            if (row - 1 >= 1) {
                targetrow = row - 1;
                ChessMove move = knightLanding(board, targetrow, targetcol, myPosition);
                if (move != null){
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    // check to see if we can actually land there
    private static ChessMove knightLanding(ChessBoard board, int targetrow, int targetcol, ChessPosition pos) {
        ChessGame.TeamColor team = board.getPiece(pos).getTeamColor();

        // identify the target square
        ChessPiece target = board.getPiece(new ChessPosition(targetrow, targetcol));

        // if we hit an empty space, add it it to the available moves
        if (target == null) {
            return new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), null);
        } else {
            if (target.getTeamColor() != team) {
                return new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), null);
            }
        }
        return null;
    }
}

