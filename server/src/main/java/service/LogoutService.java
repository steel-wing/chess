package service;

import dataAccess.*;
import model.*;
import request.LogoutRequest;

/** Handles logging out: returns a success boolean */
public class LogoutService {
    public static boolean logout(LogoutRequest logout) throws DataAccessException {
        // initialize the DAO
        AuthDAO AUTHDAO = new MemoryAuthDAO();

        // get session token and corresponding auth data
        String authToken = logout.authToken();
        AuthData auth = AUTHDAO.getAuth(authToken);

        // return false if the token is not in the AUTH table
        if (auth == null) {
            return false;
        }

        // instead of only allowing people to log in once, we could wipe
        // every entry of them from the database here

        // clear the row in the AUTH table and return success
        return AUTHDAO.deleteAuth(authToken);
    }
}
