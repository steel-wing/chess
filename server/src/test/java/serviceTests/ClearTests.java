package serviceTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import service.ClearService;

public class ClearTests {
    @AfterEach
    public void clear() {
        AuthDAO ADAO = new MemoryAuthDAO();
        GameDAO GDAO = new MemoryGameDAO();
        UserDAO UDAO = new MemoryUserDAO();
        ADAO.clear();
        GDAO.clear();
        UDAO.clear();
    }
    @Test
    public void testClear() throws TestException, DataAccessException {
        // build our DAOs
        AuthDAO ADAO = new MemoryAuthDAO();
        UserDAO UDAO = new MemoryUserDAO();
        GameDAO GDAO = new MemoryGameDAO();

        // construct some data
        String username = "The Rod";
        String password = "3141592653589793238462643383279502884197169";
        String email = "jrodham@byu.edu";
        UserData user = new UserData(username, password, email);

        // load in some data
        ADAO.createAuth(user);
        UDAO.createUser(username, user);
        GDAO.createGame("My Game Long Name");

        // clear the data
        Assertions.assertTrue(ClearService.clear());
    }

}
