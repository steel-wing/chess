package service;

import dataAccess.*;
import model.*;
import request.LoginRequest;

public class LoginService {
    /**
     * Handles logging in
     * @param loginRequest The login() request
     * @return an AuthData object
     * @throws DataAccessException If things go sour
     */
    public static AuthData login(LoginRequest loginRequest) throws DataAccessException {
        // initialize the DAOs
        AuthDAO authDao = new MemoryAuthDAO();
        UserDAO userDao = new MemoryUserDAO();

        // get login data
        String username = loginRequest.username();
        String password = loginRequest.password();

        // get corresponding User data
        UserData user = userDao.getUser(username);

        // return null if the password is incorrect
        if (!user.password().equals(password)) {
            return null;
        }

        // create a new authToken for the User
        return authDao.createAuth(user);
    }
}
