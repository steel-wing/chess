package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseDAO.DatabaseUserDAO;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.ClearService;

public class UserTests {
    // useful globals
    UserDAO userDAO = new DatabaseUserDAO();

    // build a new User
    String username = "The Rod";
    String password = "3141592653589793238462643383279502884197169";
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String sPassword = encoder.encode(password);
    String email = "jrodham@byu.edu";
    UserData data = new UserData(username, sPassword, email);

    @BeforeEach
    public void startup() throws DataAccessException {
        // put User in
        userDAO.createUser(username, data);
    }
    @AfterEach
    public void cleanup() throws DataAccessException {
        ClearService.clear();
    }

    @Test
    public void goodGetUser() throws DataAccessException {
        // get User back out
        UserData user = userDAO.getUser(data.username());

        // verify equality
        Assertions.assertEquals(user.username(), data.username());
        Assertions.assertEquals(user.password(), data.password());
        Assertions.assertEquals(user.email(), data.email());
    }
    @Test
    public void badGetUser() {
        // dummy User
        UserData dummy = new UserData("name", "pass", "email");

        // dummy not in database
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(dummy.username()));
    }

    @Test
    public void goodCreateUser() throws DataAccessException {
        // dummy User
        UserData dummy = new UserData("name", "pass", "email");

        // put User into the database
        userDAO.createUser(dummy.username(), dummy);

        // get User back out
        UserData user = userDAO.getUser(dummy.username());

        // verify equality
        Assertions.assertEquals(user.username(), dummy.username());
        Assertions.assertEquals(user.password(), dummy.password());
        Assertions.assertEquals(user.email(), dummy.email());
    }
    @Test
    public void badCreateUser() {
        // bad User
        UserData dummy = new UserData(null, null, null);

        // values cannot be null
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(dummy.username(), dummy));
    }

    @Test
    public void goodClear() throws DataAccessException {
        // load some data
        userDAO.clear();

        // try to pull User that can't exist
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(data.username()));
    }
}
