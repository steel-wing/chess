package service;

import dataAccess.*;
import model.*;
import request.LogoutRequest;

/** Handles logging out: returns a success boolean */
public class LogoutService {
    public static boolean logout(LogoutRequest logout) throws DataAccessException {
        // initialize the DAOs
        UserDAO USERDAO = new MemoryUserDAO();
        AuthDAO AUTHDAO = new MemoryAuthDAO();

        // get session token and corresponding auth data
        String authToken = logout.authToken();
        AuthData auth = AUTHDAO.getAuth(authToken);

        // return false if the token is invalid
        if (auth == null) {
            return false;
        }

        // clear the row in the AUTH table and return success
        AUTHDAO.deleteAuth(authToken);
        return true;
    }
}
