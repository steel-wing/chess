package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import request.LoginRequest;
import service.LoginService;

public class LoginTests {
    @Test
    public void testLogin() throws TestException, DataAccessException {
        String username = "davis";
        String password = "password314159265358";
        String email = "jrodham@byu.edu";
        LoginRequest userdata = new LoginRequest(username, password);
        UserData data = new UserData(username, password, email);

        UserDAO UDAO = new MemoryUserDAO();
        UDAO.createUser(username, data);

        String foundUsername = LoginService.login(userdata).username();
        Assertions.assertEquals(username, foundUsername, "The found username isn't the one that was input");
    }
}
