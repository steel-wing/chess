package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseDAO.DatabaseAuthDAO;
import dataAccess.DatabaseDAO.DatabaseUserDAO;
import dataAccess.ErrorException;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import service.RegisterService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RegisterTests {
    @AfterEach
    public void clear() throws DataAccessException {
        AuthDAO ADAO = new DatabaseAuthDAO();
        UserDAO UDAO = new DatabaseUserDAO();
        ADAO.clear();
        UDAO.clear();
    }
    @Test
    public void registerTest() throws DataAccessException, ErrorException {
        // get User data
        String username = "user";
        String password = "name";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String sPassword = encoder.encode(username + password);

        String email = "password@email.com";
        UserData user = new UserData(username, sPassword, email);

        // register User
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterService.register(request);

        // find User in the database
        UserDAO UDAO = new DatabaseUserDAO();

        UserData found = UDAO.getUser(username);

        // verify that they are the same
        Assertions.assertEquals(user.username(), found.username());
        Assertions.assertTrue(encoder.matches(username + password, user.password()));
    }

    @Test
    public void badRegister() throws DataAccessException, ErrorException {
        // get User data
        String username = "usertwo";
        String password = "nameone";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String sPassword = encoder.encode(username + password);
        String email = "passwordification@email.com";

        // register the User
        RegisterRequest request = new RegisterRequest(username, sPassword, email);
        RegisterService.register(request);

        // try to re-register the same person again
        Assertions.assertThrows(ErrorException.class, () -> RegisterService.register(request));
    }

}
