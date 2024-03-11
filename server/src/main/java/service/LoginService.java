package service;

import dataAccess.DataAccessException;
import dataAccess.ErrorException;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import request.LoginRequest;
import server.Server;

public class LoginService {
    /**
     * Handles logging in
     * @param req The login() request
     * @return an AuthData object
     * @throws DataAccessException If things go sour
     * @throws ErrorException also
     */
    public static AuthData login(LoginRequest req) throws DataAccessException, ErrorException {
        // get login data
        String username = req.username();
        String password = req.password();

        // check the input
        if (username == null || password == null) {
            throw new ErrorException("bad request");
        }

        // get corresponding User data
        UserData user = Server.userDAO.getUser(username);

        // get the password hasher
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // return null if the password salted hash is incorrect
        if (!encoder.matches(username + password, user.password())) {
            throw new ErrorException("password incorrect");
        }

        // create a new authToken for the User
        return Server.authDAO.createAuth(user);
    }
}
