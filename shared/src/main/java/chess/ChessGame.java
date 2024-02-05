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

    /////////////////
    // LEGAL MOVES //
    /////////////////

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
            if (!isInCheck(team)) {
                validMoves.add(move);
            }

            // update the last place the piece was
            lastPosition = targetPosition;
            lastTarget = targetPiece;
        }

        // restore the board to the way it was and return
        this.board = boardcopy;
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
        ChessPiece.PieceType type = board.getPiece(start).getPieceType();
        ChessPiece.PieceType promo = move.getPromotionPiece();

        // verify that it's your turn
        if (piece.getTeamColor() != turn) {
            String correctionString = "Not your turn";
            throw new InvalidMoveException(correctionString);
        }

        // verify that the move being requested is in validMoves()
        for (ChessMove validMove : validMoves(start)) {
            // remove any invalid move
            if (!move.equals(validMove)) {
                continue;
            }

            // we're valid, so make the change, paying attention to meta game rules
            board.removePiece(start);

            if (promo == null) {
                // handle the pawn deletion for en passant
                if (type == PAWN) {
                    removePawn(start, end, board);
                }

                // handle the rook teleportation for castling
                if (type == KING) {
                    teleportCastle(start, end);
                }

                // add the piece where it lands
                board.addPiece(end, piece);
            } else {
                // add the promoted piece where it lands
                board.addPiece(end, new ChessPiece(turn, promo));
            }

            // update whose turn it is, and steps (also any unkilled pawns)
            turn = turn == WHITE ? BLACK : WHITE;
            piece.stepIncrement();
            pawnStepIncrement(turn);
            return;
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
        // get the positions of all enemy pieces
        TeamColor enemyColor = teamColor == WHITE ? BLACK : WHITE;
        ArrayList<ChessPosition> enemyPositions = teamPieces(enemyColor, null);

        // get all of the positions the enemy pieces are attacking
        for (ChessPosition enemyPosition : enemyPositions) {
            ChessPiece piece = board.getPiece(enemyPosition);
            ArrayList<ChessMove> pieceMoves = piece.pieceMoves(board, enemyPosition);

            // check and see if the king is being targeted
            for (ChessMove pieceMove : pieceMoves) {
                ChessPiece target = board.getPiece(pieceMove.getEndPosition());
                // check if the square is occupied, then if it's the right type, then if it's the right team
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
        return kingChecker(teamColor, true, false);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return kingChecker(teamColor, false, false);
    }

    /**
     * Helper function to determine if the given team is in checkmate, stalemate,
     * or can mamybe castle,
     * while complying with the method standards provided
     *
     * @param teamColor self-explanatory
     * @param checkmate determines whether we're looking for checkmate or stalemate
     */
    private boolean kingChecker(TeamColor teamColor, boolean checkmate, boolean castling) {
        ChessBoard boardcopy = new ChessBoard(this.board);

        // locate the king
        ChessPosition kingPosition = teamPieces(teamColor, KING).getFirst();
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

    ////////////////
    // EN PASSANT //
    ////////////////

    /**
     *  increments the steps of all pawns who double-stepped last turn
     */
    private void pawnStepIncrement(ChessGame.TeamColor team) {
        int passantRow = team == WHITE ? 4 : 5;
        // obtain the locations of all pawns
        ArrayList<ChessPosition> pawnPositions = teamPieces(team, ChessPiece.PieceType.PAWN);

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
     */
    private void removePawn(ChessPosition start, ChessPosition end, ChessBoard board) {
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
     * @return possible enPassant attacks, empty list if none
     */
    private ArrayList<ChessMove> enPassant(ChessPosition startPosition) {
        ArrayList<ChessMove> attacks = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);
        TeamColor team = piece.getTeamColor();

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

    //////////////
    // CASTLING //
    //////////////

    /**
     * handles the movement of rooks during castling, if applicable
     */
    private void teleportCastle (ChessPosition start, ChessPosition end) {
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
    private ArrayList<ChessMove> castling(ChessPosition startPosition) {
        ArrayList<ChessMove> strafes = new ArrayList<>();
        ChessPiece King = board.getPiece(startPosition);
        TeamColor team = King.getTeamColor();

        // escape if the king has moved
        if (King.getSteps() != 0) {
            return strafes;
        }

        // ordinals
        boolean left = true;
        boolean right = true;
        int row = team == WHITE ? 1 : 8;

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
        if (isInCheck(team) || kingChecker(team, false, true)) {
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

    ///////////////
    // OVERRIDES //
    ///////////////

    // add the cool chess pieces in here
    // remember the colors are inverted in dark mode, not sure what to do about that
    // but yeah go look up the unicode stuff and instead of going to caps, add the amount
    // necessary to turn the one color into the other

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
