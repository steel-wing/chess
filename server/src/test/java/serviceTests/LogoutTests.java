package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseDAO.DatabaseAuthDAO;
import dataAccess.DatabaseDAO.DatabaseUserDAO;
import dataAccess.ErrorException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import service.LogoutService;

public class LogoutTests {
    @AfterEach
    public void clear() throws DataAccessException {
        AuthDAO ADAO = new DatabaseAuthDAO();
        UserDAO UDAO = new DatabaseUserDAO();
        ADAO.clear();
        UDAO.clear();
    }

    public AuthData login() throws DataAccessException {
        // log a User into the database
        UserDAO UDAO = new DatabaseUserDAO();
        String username = "one";
        String password = "two";
        String email = "yes@yes";
        UserData user = new UserData(username, password, email);
        AuthDAO ADAO = new DatabaseAuthDAO();
        return ADAO.createAuth(user);
    }

    @Test
    public void testLogout() throws TestException, DataAccessException, ErrorException {
        // log in
        AuthData authData = login();
        String authToken = authData.authToken();

        // log out
        LogoutService.logout(authToken);

        // definitely logged out
        Assertions.assertThrows(DataAccessException.class, () -> LogoutService.logout(authToken));
    }

    @Test
    public void badLogout() throws TestException {
        // try to log in with a bad token
        String bogusToken = "bogusauthtoken";
        Assertions.assertThrows(DataAccessException.class, () -> LogoutService.logout(bogusToken));
    }
}
