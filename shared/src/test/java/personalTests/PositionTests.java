package personalTests;


import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PositionTests {
    @Test
    public void testprinter(){
        ChessPosition newpos = new ChessPosition(1, 1);
        System.out.println(newpos);

        ChessPosition Newpos = new ChessPosition("A1");
        System.out.println(Newpos);

        Assertions.assertEquals(newpos, Newpos);
    }

}
