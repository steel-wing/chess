package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public ChessPosition(String square) {
        this.row = square.charAt(1) - '0';
        this.col = square.charAt(0) - '`';
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    public String toFancyString() {
        char c = (char)(col + '`');
        char r = (char)(row + '0');
        return String.valueOf(c) + r;
    }

    @Override
    public String toString() {
        return "{" + row + ", " + col + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
