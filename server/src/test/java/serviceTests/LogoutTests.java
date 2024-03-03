package serviceTests;

import dataAccess.*;
import dataAccess.MemoryDAO.MemoryAuthDAO;
import dataAccess.MemoryDAO.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import service.LogoutService;

public class LogoutTests {
    @AfterEach
    public void clear() {
        AuthDAO ADAO = new MemoryAuthDAO();
        UserDAO UDAO = new MemoryUserDAO();
        ADAO.clear();
        UDAO.clear();
    }

    public AuthData login() {
        // log a User into the database
        UserDAO UDAO = new MemoryUserDAO();
        String username = "one";
        String password = "two";
        String email = "yes@yes";
        UserData user = new UserData(username, password, email);
        AuthDAO ADAO = new MemoryAuthDAO();
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
