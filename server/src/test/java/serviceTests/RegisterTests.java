package serviceTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import service.RegistrationService;

public class RegisterTests {
    @AfterEach
    public void clear() {
        AuthDAO ADAO = new MemoryAuthDAO();
        UserDAO UDAO = new MemoryUserDAO();
        ADAO.clear();
        UDAO.clear();
    }
    @Test
    public void registerTest() throws DataAccessException {
        // get User data
        String username = "user";
        String password = "name";
        String email = "password@email.com";
        UserData user = new UserData(username, password, email);

        // register User
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegistrationService.register(request);

        // find User in the database
        UserDAO UDAO = new MemoryUserDAO();
        UserData found = UDAO.getUser(username);

        // verify that they are the same
        Assertions.assertEquals(user, found);
    }

    @Test
    public void badRegister() throws DataAccessException {
        // get User data
        String username = "usertwo";
        String password = "nameone";
        String email = "passwordification@email.com";

        // register the User
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegistrationService.register(request);

        // try to re-register the same person again
        Assertions.assertNull(RegistrationService.register(request));
    }

}
