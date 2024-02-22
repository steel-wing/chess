package service;

import dataAccess.*;
import model.*;
import request.LoginRequest;

/** Handles logging in: returns an authtoken for a new user */
public class LoginService {
    public static AuthData login(LoginRequest login) throws DataAccessException {
        // initialize the DAOs
        UserDAO USERDAO = new MemoryUserDAO();
        AuthDAO AUTHDAO = new MemoryAuthDAO();

        // get login data
        String username = login.username();
        String password = login.password();

        // get user data
        UserData user = USERDAO.getUser(username);

        // this will throw the exception if it arises; to be handled at the server level
        return AUTHDAO.createAuth(user);
    }
}
