package service;

import dataAccess.*;
import model.*;
import request.LoginRequest;

/** Handles logging in: returns an authtoken for a new user */
public class LoginService {
    public static AuthData login(LoginRequest login) throws DataAccessException {
        // initialize the DAOs
        UserDAO UserDAO = new MemoryUserDAO();
        AuthDAO AuthDAO = new MemoryAuthDAO();

        // get login data
        String username = login.username();
        String password = login.password();

        // get corresponding user data
        UserData user = UserDAO.getUser(username);

        // return null if the password is incorrect
        if (!user.password().equals(password)) {
            return null;
        }

        // exceptions are handled in the LoginHandler
        return AuthDAO.createAuth(user);
    }
}
