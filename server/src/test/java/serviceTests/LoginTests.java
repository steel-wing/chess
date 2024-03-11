package serviceTests;

import dataAccess.*;
import dataAccess.DatabaseDAO.DatabaseAuthDAO;
import dataAccess.DatabaseDAO.DatabaseGameDAO;
import dataAccess.DatabaseDAO.DatabaseUserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import request.LoginRequest;
import service.LoginService;

public class LoginTests {
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
    public void testLogin() throws TestException, DataAccessException, ErrorException {
        // build a new User
        String username = "The Rod";
        String password = "3141592653589793238462643383279502884197169";
        String email = "jrodham@byu.edu";
        UserData data = new UserData(username, password, email);

        // insert the User into the USER database
        UserDAO UDAO = new DatabaseUserDAO();
        UDAO.createUser(username, data);

        // log in with the username and password
        LoginRequest userdata = new LoginRequest(username, password);
        String foundUsername = LoginService.login(userdata).username();

        // verify that the found username matches the original (i.e. we get him)
        Assertions.assertEquals(username, foundUsername, "The found username isn't the one that was input");
    }

    @Test
    public void invalidLogin() throws TestException, DataAccessException {
        // build a new User
        String username = "The Rod";
        String password = "3141592653589793238462643383279502884197169";
        String email = "jrodham@byu.edu";
        UserData data = new UserData(username, password, email);

        // insert the User into the USER database
        UserDAO UDAO = new DatabaseUserDAO();
        UDAO.createUser(username, data);

        // build an unregistered User
        String badUsername = "ahahhaha";
        String badPassword = "Perry the Platapus";
        LoginRequest userdata = new LoginRequest(badUsername, badPassword);

        // verify that no user is found
        Assertions.assertThrows(DataAccessException.class, () -> LoginService.login(userdata));
    }
}
