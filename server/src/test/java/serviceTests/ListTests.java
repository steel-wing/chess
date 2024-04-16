package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseDAO.DatabaseGameDAO;
import dataAccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;

import java.util.ArrayList;

public class ListTests {
    @BeforeEach
    public void loadGames() throws DataAccessException {
        GameDAO GAME = new DatabaseGameDAO();
        GAME.clear();
        GAME.createGame("one");
        GAME.createGame("two");
        GAME.createGame("two");
        GAME.createGame("three");
        GAME.createGame("three");
        GAME.createGame("three");
    }

    @AfterEach
    public void clear() throws DataAccessException {
        GameDAO GDAO = new DatabaseGameDAO();
        GDAO.clear();
    }

    @Test
    public void testList() throws TestException, DataAccessException {
        GameDAO GDAO = new DatabaseGameDAO();
        ArrayList<GameData> list = GDAO.listGames();

        // idk if this is good practice but it does work.
        Assertions.assertEquals(6, list.size());
    }

    @Test
    public void badList() throws TestException, DataAccessException {
        GameDAO GDAO = new DatabaseGameDAO();
        ArrayList<GameData> list = GDAO.listGames();

        // even though they all have the same names, they're different.
        Assertions.assertNotEquals(3, list.size());
    }
}
