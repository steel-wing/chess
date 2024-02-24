package service;

import dataAccess.*;
import model.*;
import request.LogoutRequest;

public class LogoutService {
    /** Handles logging out
     *
     * @param logout A logout request
     * @return A success boolean
     * @throws DataAccessException
     */
    public static boolean logout(LogoutRequest logout) throws DataAccessException {
        // initialize the DAO
        AuthDAO authDao = new MemoryAuthDAO();

        // get session token and corresponding auth data
        String authToken = logout.authToken();
        AuthData authData = authDao.getAuth(authToken);

        // return false if the token is not in the AUTH table
        if (authData == null) {
            return false;
        }

        // clear the row in the AUTH table and return success
        return authDao.deleteAuth(authToken);
    }
}
