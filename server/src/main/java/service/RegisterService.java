package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import server.Server;

public class RegisterService {
    /**
     * Handles Registration
     * @param registerRequest The register() request
     * @return An AuthData object
     * @throws DataAccessException If things go sour
     */
    public static AuthData register (RegisterRequest registerRequest) throws DataAccessException {
        // take apart the request and load that data into a userdata (even though they're the same)
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        UserData user = new UserData(username, password, email);

        // verify that the User doesn't already exist
        UserData found = null;
        try {
            found = Server.userDAO.getUser(username);
        } catch (DataAccessException ignored) {}

        if (found != null) {
            return null;
        }

        // add the user to the USER database
        Server.userDAO.createUser(username, user);

        // return the new authData for this User
        return Server.authDAO.createAuth(user);
    }
}
