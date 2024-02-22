package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;

/** Handles logging in: returns an authtoken for a new user */
public class LoginService {
    public AuthData login(LoginRequest login) throws DataAccessException {
        // initialize the DAOs
        UserDAO USERDAO = new MemoryUserDAO();
        AuthDAO AUTHDAO = new MemoryAuthDAO();

        // get login data
        String username = login.username();
        String password = login.password();

        // get user data (it's okay if it's null)
        UserData user = USERDAO.getUser(username);

        // this will throw the exception if it arises; to be handled at the server level
        return AUTHDAO.createAuth(user);
    }
}
