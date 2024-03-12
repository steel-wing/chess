package dataAccessTests;

import chess.ChessGame;
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
    public void badGetGame() {
        // dummy data
        GameData dummy = new GameData(0, "", "", "dummy", new ChessGame());

        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(dummy.gameID()));
    }

    @Test
    public void goodCreateGame() throws DataAccessException {
        // game data
        GameData game = gameDAO.createGame("newgame");

        // verify that the game exists by checking its ID
        Assertions.assertEquals(gameDAO.getGame(game.gameID()).gameID(), game.gameID());
    }
    @Test
    public void badCreateGame() {
        // try to make a game with no name
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    public void goodListGame() {}
    @Test
    public void badListGame() {}

    @Test
    public void goodUpdateGame() {}
    @Test
    public void badUpdateGame() {}

    @Test
    public void goodClearGame() {}
}
