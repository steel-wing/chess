package chess;

import java.util.ArrayList;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;

/**
 * A class that handles the meta-game motion of pawns: <p>
 * Implements en Passant
 */
public class PawnMetaMotion {

    private final ChessGame game;
    private final ChessBoard board;

    public PawnMetaMotion(ChessGame game) {
        this.game = game;
        this.board = game.getBoard();
    }

    /**
     *  increments the steps of all pawns who double-stepped last turn
     *
     * @param team The team of the pawns that need step-incrementation
     */
    public void pawnStepIncrement(ChessGame.TeamColor team) {
        int passantRow = team == WHITE ? 4 : 5;
        // obtain the locations of all pawns
        ArrayList<ChessPosition> pawnPositions = game.teamPieces(team, ChessPiece.PieceType.PAWN);

        // update their steps to 2 if they just double-jumped
        for (ChessPosition pawnPosition : pawnPositions) {
            if (pawnPosition.getRow() == passantRow &&
                    board.getPiece(pawnPosition).getSteps() == 1) {
                board.getPiece(pawnPosition).stepIncrement();
            }
        }
    }

    /**
     * handles the removal of a pawn capture by en passant
     *
     * @param move The move taking place
     */
    public void removePawn(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessGame.TeamColor turn = game.getTeamTurn();

        // verify a pawn has performed en passant
        if (start.getRow() != end.getRow() &&
                start.getColumn() != end.getColumn() &&
                board.getPiece(end) == null) {
            int dir = turn == WHITE ? 1 : -1;
            board.removePiece(new ChessPosition(end.getRow() - dir, end.getColumn()));
        }
    }

    /**
     * Produces possible en passant moves if any are available
     *
     * @param startPosition The position of our attacking pawn, originally
     * @return Possible enPassant attacks, empty list if none
     */
    public ArrayList<ChessMove> enPassant(ChessPosition startPosition) {
        ArrayList<ChessMove> attacks = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);
        ChessGame.TeamColor team = piece.getTeamColor();

        int row = startPosition.getRow();
        int col = startPosition.getColumn();
        int dir = team == WHITE ? 1 : -1;

        // check to see if we're in the enPassant-able row for our color
        int attackRow = team == WHITE ? 5 : 4;
        if (row != attackRow) {
            return attacks;
        }

        // check to see if the pawn to our right has only moved once
        if (col < 8) {
            ChessPiece target = board.getPiece(new ChessPosition(row, col + 1));
            if (target != null &&
                    target.getTeamColor() != team &&
                    target.getPieceType() == PAWN &&
                    target.getSteps() == 1) {
                attacks.add(new ChessMove(startPosition, new ChessPosition(row + dir, col + 1), null));
            }
        }

        // check to see if the pawn to our left has only moved once
        if (col > 1) {
            ChessPiece target = board.getPiece(new ChessPosition(row, col - 1));
            if (target != null &&
                    target.getTeamColor() != team &&
                    target.getPieceType() == PAWN &&
                    target.getSteps() == 1) {
                attacks.add(new ChessMove(startPosition, new ChessPosition(row + dir, col - 1), null));
            }
        }
        return attacks;
    }
}
