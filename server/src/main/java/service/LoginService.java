package service;

import dataAccess.*;
import model.*;
import request.LoginRequest;
import server.Server;

public class LoginService {
    /**
     * Handles logging in
     * @param loginRequest The login() request
     * @return an AuthData object
     * @throws DataAccessException If things go sour
     */
    public static AuthData login(LoginRequest loginRequest) throws DataAccessException {
        // get login data
        String username = loginRequest.username();
        String password = loginRequest.password();

        // get corresponding User data
        UserData user = Server.userDAO.getUser(username);

        // return null if the password is incorrect
        if (!user.password().equals(password)) {
            return null;
        }

        // create a new authToken for the User
        return Server.authDAO.createAuth(user);
    }
}
