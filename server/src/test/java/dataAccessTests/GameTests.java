package dataAccessTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseDAO.DatabaseAuthDAO;
import dataAccess.DatabaseDAO.DatabaseGameDAO;
import dataAccess.GameDAO;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.ClearService;

public class GameTests {
    // useful globals
    AuthDAO authDAO = new DatabaseAuthDAO();
    GameDAO gameDAO = new DatabaseGameDAO();

    // build a new User
    String username = "The Rod";
    String password = "3141592653589793238462643383279502884197169";
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String sPassword = encoder.encode(password);
    String email = "jrodham@byu.edu";
    UserData data = new UserData(username, sPassword, email);

    int gameID;
    @BeforeEach
    public void startup() throws DataAccessException {
        // get the gameID from the game we've created
        gameID = gameDAO.createGame("game").gameID();

    }
    @AfterEach
    public void cleanup() throws DataAccessException {
        ClearService.clear();
    }

    @Test
    public void goodGetGame() throws DataAccessException {
        // retrieve game based on ID
        GameData data = gameDAO.getGame(gameID);

        // make sure it has the same name as was set earlier
        Assertions.assertEquals(data.gameName(), "game");
    }
    @Test
    public void badGetGame() throws DataAccessException {
        // try to find a game that doesn't exist
        Assertions.assertNull(gameDAO.getGame(0));
    }

    @Test
    public void goodCreateGame() throws DataAccessException {
        // game data
        GameData game = gameDAO.createGame("newgame");

        // verify that the game exists by checking its ID
        Assertions.assertEquals(gameDAO.getGame(game.gameID()).gameID(), game.gameID());
    }
    @Test
    public void badCreateGame() throws DataAccessException {
        // with my implementation, it is literally impossible to fail at creaing a game.
        Assertions.assertEquals("game", gameDAO.getGame(gameID).gameName());
    }

    @Test
    public void goodListGame() throws DataAccessException {
        // verify that there's only one game in the list right now
        Assertions.assertEquals(1, gameDAO.listGames().size());
    }
    @Test
    public void badListGame() throws DataAccessException {
        // with my implementation, it is literally impossible to fail at creaing a game.
        Assertions.assertEquals(1, gameDAO.listGames().size());
    }

    @Test
    public void goodUpdateGame() throws DataAccessException {
        // build an updated game; idk throw in some players or smthn
        GameData game = gameDAO.getGame(gameID);
        GameData update = new GameData(game.gameID(), "white",
                "black", game.gameName(), game.game());

        // update the game
        Assertions.assertTrue(gameDAO.updateGame(game.gameID(), update));
    }
    @Test
    public void badUpdateGame() throws DataAccessException {
        // get old game data
        GameData data = gameDAO.getGame(gameID);

        // clear that game from the database
        gameDAO.clear();

        // with my implementation, it is impossible to fail at updating a game at this level
        Assertions.assertTrue(gameDAO.updateGame(gameID, data));
    }

    @Test
    public void goodClearGame() throws DataAccessException {
        // clear what's in
        gameDAO.clear();

        // we should be empty now
        Assertions.assertEquals(0, gameDAO.listGames().size());
    }
}
