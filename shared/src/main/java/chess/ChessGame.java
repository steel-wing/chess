package chess;

import java.util.ArrayList;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * any signatures of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
        this.turn = WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return ArrayList of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public ArrayList<ChessMove> validMoves(ChessPosition startPosition) {
        // quick escape condition in case this has been called on nothing
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        if (board.getPiece(startPosition) == null) {
            return validMoves;
        }

        // copy everything down
        ChessBoard boardcopy = new ChessBoard(this.board);
        ChessPiece piece = board.getPiece(startPosition);
        TeamColor team = piece.getTeamColor();

        // find all possible moves the piece could physically make
        ArrayList<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);

        // include enPassant and castling, if relevant
        if (piece.getPieceType() == PAWN) {
            possibleMoves.addAll(enPassant(startPosition));
        }
        if (piece.getPieceType() == KING) {
            possibleMoves.addAll(castling(startPosition));
        }

        // save info about hypothetical motion
        ChessPosition lastPosition = startPosition;
        ChessPiece lastTarget = null;

        // for all moves, move the piece there
        for (ChessMove move : possibleMoves){
            ChessPosition targetPosition = move.getEndPosition();
            ChessPiece targetPiece = board.getPiece(targetPosition);

            // move the piece to its new location
            board.removePiece(lastPosition);
            board.addPiece(targetPosition, piece);

            // replace the taken material, if there was any
            if (lastTarget != null) {
                board.addPiece(lastPosition, lastTarget);
            }

            // if we aren't in check now, the move is valid
            if(!isInCheck(team)){
                validMoves.add(move);
            }

            // update the last place the piece was
            lastPosition = targetPosition;
            lastTarget = targetPiece;
        }

        // restore the board to the way it was and return
        this.board = boardcopy;

        System.out.println(piece + " at " + startPosition + " with " + piece.getSteps() + " step(s)");
        System.out.println("can move to " + validMoves);

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);
        ChessPiece.PieceType type = move.getPromotionPiece();

        // verify that it's your turn
        if (piece.getTeamColor() != turn) {
            String correctionString = "Not your turn";
            throw new InvalidMoveException(correctionString);
        }

        // verify that the move being requested is in validMoves()
        for (ChessMove validMove : validMoves(start)) {
            if (move.equals(validMove)) {
                // we're valid, so make the change, paying attention to pawn promotion
                board.removePiece(start);
                if (type == null) {
                    removePassant(move);
                    // add the piece where it lands
                    board.addPiece(end, piece);
                } else {
                    // add the promoted piece where it lands
                    board.addPiece(end, new ChessPiece(turn, type));
                }

                // update whose turn it is, and steps (also any unkilled pawns)
                turn = turn == WHITE ? BLACK : WHITE;
                piece.stepIncrement();
                pawnStepIncrement();

                System.out.println(piece + " moved to " + end + " with " + piece.getSteps() + " step(s)");

                return;
            }
        }
        // if all failed, throw an exception
        String correctionString = "Invalid move requested";
        throw new InvalidMoveException(correctionString);
    }

    /**
     * goes through and finds all of the LOCATIONS of pieces of a type (or all if null) on a team
     *
     * @param teamColor which team we're looking at
     * @param pieceType what kind of piece we're looking at (null means we don't care)
     * @return ArrayList of *ChessPositions*
     */
    private ArrayList<ChessPosition> teamPieces(TeamColor teamColor, ChessPiece.PieceType pieceType) {
        ArrayList<ChessPosition> places = new ArrayList<>();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                // skip if empty
                if (piece == null) {
                    continue;
                }
                // if we find a piece, check its team and type (accept it if we're looking for all)
                if (piece.getTeamColor() == teamColor && (pieceType == null || piece.getPieceType() == pieceType)) {
                    places.add(new ChessPosition(row, col));
                }
            }
        }
        return places;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // get all the positions of enemy pieces
        TeamColor enemyColor = teamColor == WHITE ? BLACK : WHITE;
        ArrayList<ChessPosition> enemyPositions = teamPieces(enemyColor, null);

        // get all of the positions the enemy pieces are attacking
        for (ChessPosition enemyPosition : enemyPositions) {
            ChessPiece piece = board.getPiece(enemyPosition);
            ArrayList<ChessMove> pieceMoves = piece.pieceMoves(board, enemyPosition);

            // check and see if the king is being targeted
            for (ChessMove pieceMove : pieceMoves) {
                ChessPiece target = board.getPiece(pieceMove.getEndPosition());
                // check if the square is occupied, then if its the right type, then if its the right team
                if (target != null &&
                        target.getPieceType() == ChessPiece.PieceType.KING &&
                        target.getTeamColor() == teamColor) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return kingChecker(teamColor, true);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return kingChecker(teamColor, false);
    }

    /**
     * Helper function to determine if the given team is in checkmate or stalemate,
     * while complying with the method standards provided
     *
     * @param teamColor self-explanatory
     * @param checkmate determines whether we're looking for checkmate or stalemate
     */
    private boolean kingChecker(TeamColor teamColor, boolean checkmate) {
        ChessBoard boardcopy = new ChessBoard(this.board);

        // locate the king
        ChessPosition kingPosition = teamPieces(teamColor, KING).getFirst();
        ChessPiece King = board.getPiece(kingPosition);

        // find all options available to the king (including staying where he is, if checkmate)
        ArrayList<ChessMove> kingOptions = King.pieceMoves(board, kingPosition);
        if (checkmate) {
            kingOptions.add(new ChessMove(kingPosition, kingPosition, null));
        }

        // save info about king's hypothetical movement
        ChessPosition lastPosition = kingPosition;
        ChessPiece lastTarget = null;

        // for all moves, move the piece there
        for (ChessMove option : kingOptions){
            ChessPosition targetPosition = option.getEndPosition();
            ChessPiece targetPiece = board.getPiece(targetPosition);

            // move the piece to its new location
            board.removePiece(lastPosition);
            board.addPiece(targetPosition, King);

            // replace the taken material, if there was any
            if (lastTarget != null) {
                board.addPiece(lastPosition, lastTarget);
            }

            // if we aren't in check now, the move is valid
            if(!isInCheck(teamColor)){
                return false;
            }

            // update the last place the king was
            lastPosition = targetPosition;
            lastTarget = targetPiece;
        }

        // restore the board to the way it was and return
        this.board = boardcopy;
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }


    /**
     *  increments the steps of all pawns who enPassanted last turn
     */
    private void pawnStepIncrement() {
        int passantRow = turn == WHITE ? 4 : 5;

        ArrayList<ChessPosition> pawnPositions = teamPieces(turn, ChessPiece.PieceType.PAWN);
        for (ChessPosition pawnPosition : pawnPositions) {
            if (pawnPosition.getRow() == passantRow &&
                    board.getPiece(pawnPosition).getSteps() == 1) {
                board.getPiece(pawnPosition).stepIncrement();
            }
        }
    }

    /**
     * removes the piece that was killed in an en passant attack
     */
    private void removePassant(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        // handle the en passant case for deletion
        if (piece.getPieceType() == PAWN &&
                start.getRow() != end.getRow() &&
                start.getColumn() != end.getColumn() &&
                board.getPiece(end) == null) {
            int dir = turn == WHITE ? 1 : -1;
            System.out.println("removing piece at position " + new ChessPosition(end.getRow() - dir, end.getColumn()));
            board.removePiece(new ChessPosition(end.getRow() - dir, end.getColumn()));
        }
    }


    /**
     * figures out if an en passant move can happen, and returns the options
     * @return possible enPassant attacks, empty list if none
     */
    private ArrayList<ChessMove> enPassant(ChessPosition startPosition) {
        ArrayList<ChessMove> attacks = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);
        TeamColor team = piece.getTeamColor();

        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        // check to see if we're in the enPassant-able row for our color
        int attackRow = team == WHITE ? 5 : 4;
        if (row != attackRow) {
            return attacks;
        }

        int dir = team == WHITE ? 1 : -1;

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


    private ArrayList<ChessMove> castling(ChessPosition startPosition) {
        ArrayList<ChessMove> strafes = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);
        TeamColor team = piece.getTeamColor();

        return strafes;
    }

    @Override
    public String toString() {
        ChessBoard board = this.board;

        StringBuilder output = new StringBuilder();
        output.append("   ");
        for (int col = 1; col <= 8; col++) {
            output.append((char)(col + '`'));
            output.append(" ");
        }
        output.append("\n");

        // iterate across the board and add all pieces while delimiting with "|"
        for (int row = 8; row >= 1; row--){
            output.append((char)(row + '0'));
            output.append(" |");
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece == null){
                    output.append(" |");
                    continue;
                }
                // white pieces go uppercase
                Character type = ChessBoard.TypetocharMap.get(piece.getPieceType());
                type = piece.getTeamColor() == WHITE ? Character.toUpperCase(type) : type;
                output.append(type);
                output.append("|");
            }
            output.append(' ');
            output.append((char)(row + '0'));
            output.append("\n");
        }
        output.append("   ");
        for (int col = 1; col <= 8; col++) {
            output.append((char)(col + '`'));
            output.append(" ");
        }
        return output.toString();
    }
}
