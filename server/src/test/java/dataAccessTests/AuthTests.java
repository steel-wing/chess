package dataAccessTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseDAO.DatabaseAuthDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.ClearService;

public class AuthTests {
    // useful globals
    AuthDAO authDAO = new DatabaseAuthDAO();

    // build a new User
    String username = "The Rod";
    String password = "3141592653589793238462643383279502884197169";
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String sPassword = encoder.encode(password);
    String email = "jrodham@byu.edu";
    UserData data = new UserData(username, sPassword, email);

    String authToken;
    @BeforeEach
    public void startup() throws DataAccessException {
        // put User in
        authToken = authDAO.createAuth(data).authToken();
    }
    @AfterEach
    public void cleanup() throws DataAccessException {
        ClearService.clear();
    }

    @Test
    public void goodGetAuth() throws DataAccessException {
        // get the authdata stored
        AuthData auth = authDAO.getAuth(authToken);

        // verify that it was stored
        Assertions.assertEquals(auth.authToken(), authToken);
    }
    @Test
    public void badGetAuth() {
        // dummy auth
        String dummy = "dummy authToken";

        // retrieve dummy and fail
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(dummy));
    }

    @Test
    public void goodCreateAuth() throws DataAccessException {
        // dummy User
        UserData dummy = new UserData("name", "pass", "email");

        // create auth data (and store it)
        AuthData auth = authDAO.createAuth(dummy);

        // verify that the data has been found
        Assertions.assertEquals(auth, authDAO.getAuth(auth.authToken()));
    }
    @Test
    public void badCreateAuth() {
        // bad User
        UserData dummy = new UserData(null, null, null);

        // can't have username = null
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(dummy));
    }

    @Test
    public void goodDeleteAuth() throws DataAccessException {
        // delete the user in there
        authDAO.deleteAuth(authToken);

        // verify deletion by error
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(authToken));

    }
    @Test
    public void badDeleteAuth() {
        // try to delete an auth that doesn't exist
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("dummy"));
    }

    @Test
    public void goodClearAuth() throws DataAccessException {
        // clear all data
        authDAO.clear();

        // try to pull auth that can't exist
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(authToken));
    }
}
