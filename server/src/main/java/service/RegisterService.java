package service;

import dataAccess.DataAccessException;
import dataAccess.ErrorException;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import request.RegisterRequest;
import server.Server;

public class RegisterService {
    /**
     * Handles Registration
     * @param req The register() request
     * @return An AuthData object
     * @throws DataAccessException If things go sour
     */
    public static AuthData register (RegisterRequest req) throws DataAccessException, ErrorException {
        // take apart the request and load that data into a userdata (even though they're the same)
        String username = req.username();
        String password = req.password();
        String email = req.email();

        // check the input
        if (username == null || password == null || email == null) {
            throw new ErrorException("null value");
        }

        // get the password salted hash
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String sPassword = encoder.encode(username + password);

        UserData user = new UserData(username, sPassword, email);

        // verify that the User doesn't already exist
        UserData found;
        // try/catch to ignore the expected Exception
        try {
            found = Server.userDAO.getUser(username);
        } catch (DataAccessException ignored) {
            found = null;
        }

        if (found != null) {
            throw new ErrorException("User already registered");
        }

        // add the user to the USER database
        Server.userDAO.createUser(username, user);

        // return the new authData for this User
        return Server.authDAO.createAuth(user);
    }
}
