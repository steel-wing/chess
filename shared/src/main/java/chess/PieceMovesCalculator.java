package chess;

import java.util.ArrayList;

public class PieceMovesCalculator {
    /**
     * Delegates a request for the motion of a piece to the piece it belongs to
     * @return Collection of valid moves
     */
    public static ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos, ChessPiece.PieceType type){
        if (type == ChessPiece.PieceType.PAWN){
            return PawnMovesCalculator.pieceMoves(board, pos);
        } else if (type == ChessPiece.PieceType.KNIGHT){
            return KnightMovesCalculator.pieceMoves(board, pos);
        } else if (type == ChessPiece.PieceType.BISHOP){
            return BishopMovesCalculator.pieceMoves(board, pos);
        } else if (type == ChessPiece.PieceType.ROOK){
            return RookMovesCalculator.pieceMoves(board, pos);
        } else if (type == ChessPiece.PieceType.QUEEN){
            return QueenMovesCalculator.pieceMoves(board, pos);
        } else if (type == ChessPiece.PieceType.KING){
            return KingMovesCalculator.pieceMoves(board, pos);
        }
        // this is just here since Java yells at me if it isn't here
        return null;
    }

    /**
     * @param dir Up/Down
     * @param bias Right/Left
     * @param steps number of hops, max = 8
     * Calculates a list of moves in the taxicab direction indicated by dir and bias.
     * Goes until hitting an enemy, a wall, or running out of steps.
     * @return A list of moves in the line
     */
    public static ArrayList<ChessMove> linearMotion(ChessPosition pos, int dir, int bias, int steps, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team = board.getPiece(pos).getTeamColor();

        int row = pos.getRow();
        int col = pos.getColumn();

        int targetrow = row + dir;
        int targetcol = col + bias;

        // while in the bounds of the board, and not overstepping
        while ((1 <= targetrow) && (targetrow <= board.rows) && (1 <= targetcol) && (targetcol <= board.cols) && (steps > 0)) {

            // identify the target square
            ChessPiece target = board.getPiece(new ChessPosition(targetrow, targetcol));

            // if we hit an empty space, add it to the available moves
            if (target == null) {
                moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), null));

                // iterate onwards in the direction indicated by dir and bias
                targetrow += dir;
                targetcol += bias;
                steps--;
            } else {
                // if we hit a piece we go no further. Check to see if it's an enemy: if so, we can hit it
                if (target.getTeamColor() != team) {
                    moves.add(new chess.ChessMove(pos, new ChessPosition(targetrow, targetcol), null));
                }
                break;
            }
        }
        return moves;
    }
}
