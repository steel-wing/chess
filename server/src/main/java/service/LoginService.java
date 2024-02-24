package service;

import dataAccess.*;
import model.*;
import request.LoginRequest;

public class LoginService {
    /**
     * Handles logging in
     * @param login
     * @return an AuthData object
     * @throws DataAccessException
     */
    public static AuthData login(LoginRequest login) throws DataAccessException {
        // initialize the DAOs
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDao = new MemoryAuthDAO();

        // get login data
        String username = login.username();
        String password = login.password();

        // get corresponding User data
        UserData user = userDao.getUser(username);

        // return null if the password is incorrect
        if (!user.password().equals(password)) {
            return null;
        }

        // if the User is already in the database, return their data
        AuthData found = authDao.getAuthFromUser(username);
        if (found != null) {
            return found;
        }

        // create a new authToken for the User
        return authDao.createAuth(user);
    }
}
