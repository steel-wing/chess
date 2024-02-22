package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import request.LoginRequest;
import service.LoginService;

public class LoginTests {
    @AfterEach
    public void clearify() {
        UserDAO UDAO = new MemoryUserDAO();
        System.out.println(UDAO.clear());
    }

    @Test
    public void testNormalLogin() throws TestException, DataAccessException {
        String username = "The Rod";
        String password = "3141592653589793238462643383279502884197169";
        String email = "jrodham@byu.edu";
        LoginRequest userdata = new LoginRequest(username, password);
        UserData data = new UserData(username, password, email);

        UserDAO UDAO = new MemoryUserDAO();
        UDAO.createUser(username, data);

        String foundUsername = LoginService.login(userdata).username();
        Assertions.assertEquals(username, foundUsername, "The found username isn't the one that was input");

        UDAO.clear();
    }
}
