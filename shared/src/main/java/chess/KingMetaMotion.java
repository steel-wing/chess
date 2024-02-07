package chess;

import java.util.ArrayList;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;

/**
 * A class that handles the meta-game motion of kings: <p>
 * implements Castling and kingChecker(),
 * a useful helper function for Check, Stalemate, and Checkmate
 */
public class KingMetaMotion {
    /**
     * Helper function to determine if the given team is in checkmate, stalemate,
     * or can mamybe castle,
     * while complying with the method standards provided
     *
     * @param teamColor self-explanatory
     * @param checkmate determines whether we're looking for checkmate or stalemate
     * @param castling determines if we're checking if we can castle
     * @return True IFF the requested move CANNOT be completed
     */
    public static boolean kingChecker(ChessGame.TeamColor teamColor, boolean checkmate, boolean castling, ChessGame game) {
        ChessBoard board = game.getBoard();
        ChessBoard boardcopy = new ChessBoard(board);

        // locate the king
        ChessPosition kingPosition = game.teamPieces(teamColor, KING).getFirst();
        ChessPiece King = board.getPiece(kingPosition);

        // find all options available to the king (including his location, if checking for mate)
        ArrayList<ChessMove> kingOptions = King.pieceMoves(board, kingPosition);
        if (checkmate) {
            kingOptions.add(new ChessMove(kingPosition, kingPosition, null));
        }

        // neat little removal system to check only the flanks for castling
        if (castling) {
            kingOptions.removeIf(move -> move.getEndPosition().getRow() != kingPosition.getRow());
        }

        // save info about king's hypothetical movement
        ChessPosition lastPosition = kingPosition;
        ChessPiece lastTarget = null;

        // for all moves, move the piece there
        for (ChessMove option : kingOptions){
            ChessPosition targetPosition = option.getEndPosition();
            ChessPiece targetPiece = board.getPiece(targetPosition);

            // if the position is blocked, for castling, skip it
            if (castling && targetPiece != null) {
                continue;
            }

            // move the piece to its new location
            board.removePiece(lastPosition);
            board.addPiece(targetPosition, King);

            // replace the taken material, if there was any
            if (lastTarget != null) {
                board.addPiece(lastPosition, lastTarget);
            }

            // if we aren't in check now, the move is valid
            if(!game.isInCheck(teamColor)){
                return false;
            }

            // update the last place the king was
            lastPosition = targetPosition;
            lastTarget = targetPiece;
        }

        // restore the board to the way it was and return
        game.setBoard(boardcopy);
        return true;
    }

    /**
     * handles the movement of rooks during castling, if applicable
     * @param move The move taking place
     */
    public static void teleportCastle (ChessMove move, ChessGame game) {
        ChessBoard board = game.getBoard();
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessGame.TeamColor turn = game.getTeamTurn();

        int row = start.getRow();
        // check to see if the king just moved two spaces right
        if (end.getColumn() - start.getColumn() == 2) {
            // delete the right rook, and put it on the other side of the king
            board.removePiece(new ChessPosition(row, 8));
            board.addPiece(new ChessPosition(row, 6), new ChessPiece(turn, ROOK));
        }
        // check to see if the king just moved two spaces left
        if (end.getColumn() - start.getColumn() == -2) {
            // delete the right rook, and put it on the other side of the king
            board.removePiece(new ChessPosition(row, 1));
            board.addPiece(new ChessPosition(row, 4), new ChessPiece(turn, ROOK));
        }
    }

    /**
     *  implements castling
     * @return a list of possible castle-moves for the king
     */
    public static ArrayList<ChessMove> castling(ChessPosition startPosition, ChessGame game) {
        ChessBoard board = game.getBoard();
        ArrayList<ChessMove> strafes = new ArrayList<>();
        ChessPiece King = board.getPiece(startPosition);
        ChessGame.TeamColor team = King.getTeamColor();

        // ordinals
        boolean left = true;
        boolean right = true;
        int row = team == WHITE ? 1 : 8;

        // escape if the king has moved
        if (King.getSteps() != 0 || startPosition.getColumn() != 5 || startPosition.getRow() != row) {
            return strafes;
        }

        // read the row, and see which direction, if any, we can castle
        for (int col = 1; col <= 8; col++) {
            ChessPiece select = board.getPiece(new ChessPosition(row, col));
            if (select == null) {
                continue;
            }
            // remove any direction with anything other than a rook or king in it
            ChessPiece.PieceType target = select.getPieceType();
            if (target != KING && target != ROOK) {
                if (col < 5) {
                    left = false;
                }
                if (col > 5) {
                    right = false;
                }
            }
        }

        // leave if the king, or both of his flanks, are in check
        if (game.isInCheck(team) || kingChecker(team, false, true, game)) {
            return strafes;
        }

        // check right flank
        if (right) {
            // escape if the rook has moved
            ChessPiece rightRook = board.getPiece(new ChessPosition(row, 8));
            if (rightRook == null || rightRook.getSteps() != 0) {
                return strafes;
            }
            // otherwise, teleport the rook
            ChessPosition strafeRight = new ChessPosition(row, 7);
            strafes.add(new ChessMove(startPosition, strafeRight, null));
        }

        // check left flank
        if (left) {
            // escape if the rook has moved
            ChessPiece leftRook = board.getPiece(new ChessPosition(row, 1));
            if (leftRook == null || leftRook.getSteps() != 0) {
                return strafes;
            }
            // otherwise, teleport the rook
            ChessPosition strafeLeft = new ChessPosition(row, 3);
            strafes.add(new ChessMove(startPosition, strafeLeft, null));
        }
        return strafes;
    }
}
