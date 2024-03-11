package serviceTests;

import dataAccess.*;
import dataAccess.DatabaseDAO.DatabaseAuthDAO;
import dataAccess.DatabaseDAO.DatabaseGameDAO;
import dataAccess.DatabaseDAO.DatabaseUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import request.JoinRequest;
import service.JoinService;

public class JoinTests {
    @AfterEach
    public void clear() throws DataAccessException {
        AuthDAO ADAO = new DatabaseAuthDAO();
        GameDAO GDAO = new DatabaseGameDAO();
        UserDAO UDAO = new DatabaseUserDAO();
        ADAO.clear();
        GDAO.clear();
        UDAO.clear();
    }

    @Test
    public void testJoin() throws TestException, DataAccessException, ErrorException {
        // build a new User
        String username = "The Rod";
        String password = "3141592653589793238462643383279502884197169";
        String email = "jrodham@byu.edu";
        UserData data = new UserData(username, password, email);

        // insert the User into the USER database
        UserDAO UDAO = new DatabaseUserDAO();
        UserData user = UDAO.createUser(username, data);

        // get an authToken for the User
        AuthDAO ADAO = new DatabaseAuthDAO();
        AuthData authData = ADAO.createAuth(user);

        // build a game, get it's ID
        GameDAO GDAO = new DatabaseGameDAO();
        int gameID = GDAO.createGame("gamed fdf name").gameID();

        // have the user join the game as White
        JoinRequest joinRequest = new JoinRequest("WHITE", gameID, authData.authToken());
        Assertions.assertTrue(JoinService.join(joinRequest));
    }

    @Test
    public void invalidJoin() throws TestException, DataAccessException {
        // build a new User
        String username = "The Man";
        String password = "mmmmmmm";
        String email = "baba@baba";
        UserData data = new UserData(username, password, email);

        // insert the User into the USER database
        UserDAO UDAO = new DatabaseUserDAO();
        UserData user = UDAO.createUser(username, data);

        // get an authToken for the User
        AuthDAO ADAO = new DatabaseAuthDAO();
        AuthData authData = ADAO.createAuth(user);

        // build a game, get it's ID
        GameDAO GDAO = new DatabaseGameDAO();
        int gameID = GDAO.createGame("gamename").gameID();

        // have the user join the game with a bad ID
        JoinRequest joinRequest = new JoinRequest("BLACK", 12345, username);
        Assertions.assertThrows(DataAccessException.class, () -> JoinService.join(joinRequest));
    }
}
