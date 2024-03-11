package serviceTests;

import dataAccess.*;
import dataAccess.DatabaseDAO.DatabaseUserDAO;
import dataAccess.MemoryDAO.MemoryAuthDAO;
import dataAccess.MemoryDAO.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import request.CreateRequest;
import service.CreateService;

public class CreateTests {
    @AfterEach
    public void clear() throws DataAccessException {
        AuthDAO ADAO = new MemoryAuthDAO();
        GameDAO GDAO = new MemoryGameDAO();
        UserDAO UDAO = new DatabaseUserDAO();
        ADAO.clear();
        GDAO.clear();
        UDAO.clear();
    }
    @Test
    public void testCreate() throws TestException, DataAccessException, ErrorException {
        // get an authToken for a User
        String username = "Ttimm";
        String password = "848484";
        String email = "what@byu.edu";
        UserData user = new UserData(username, password, email);
        AuthDAO ADAO = new MemoryAuthDAO();
        AuthData authData = ADAO.createAuth(user);
        String authToken = authData.authToken();

        // make a game using our User
        GameDAO GDAO = new MemoryGameDAO();
        CreateRequest request = new CreateRequest("GAME HAHA", authToken);

        // create a game and get its ID
        int gameID = CreateService.create(request);
        GameData game = GDAO.getGame(gameID);

        // verify... that the ID equals itself?
        Assertions.assertEquals(gameID, game.gameID());
    }

    @Test
    public void badCreate() throws TestException {
        // get an authToken for a User
        String username = "The Rod";
        String password = "31";
        String email = "jrodham@byu.edu";
        UserData user = new UserData(username, password, email);
        AuthDAO AUTH = new MemoryAuthDAO();
        AuthData authData = AUTH.createAuth(user);
        String authToken = authData.authToken();

        // make request
        CreateRequest request = new CreateRequest(null, authToken);

        // try to make a game with no name using our User
        Assertions.assertThrows(ErrorException.class, () -> CreateService.create(request));
    }
}
