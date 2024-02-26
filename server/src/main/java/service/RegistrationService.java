package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;

public class RegistrationService {
    /**
     * Handles Registration
     * @param registerRequest The register() request
     * @return An AuthData object
     * @throws DataAccessException If things go sour
     */
    public static AuthData register (RegisterRequest registerRequest) throws DataAccessException {
        // initialize the DAOs
        AuthDAO authDao = new MemoryAuthDAO();
        UserDAO userDao = new MemoryUserDAO();

        // take apart the request and load that data into a userdata (even though they're the same)
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        UserData user = new UserData(username, password, email);

        // verify that the User doesn't already exist
        UserData found = null;
        try {
            found = userDao.getUser(username);
        } catch (DataAccessException ignored) {}

        if (found != null) {
            return null;
        }

        // add the user to the USER database
        userDao.createUser(username, user);

        // return the new authData for this User
        return authDao.createAuth(user);
    }
}
