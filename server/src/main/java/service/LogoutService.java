package service;

import dataAccess.*;
import model.*;

public class LogoutService {
    /** Handles logging out
     *
     * @param authToken the incoming authToken
     * @return A success boolean
     * @throws DataAccessException If things go sour
     */
    public static boolean logout(String authToken) throws DataAccessException {
        // initialize the DAO
        AuthDAO authDao = new MemoryAuthDAO();

        // get auth data (and throw errors)
        AuthData authData = authDao.getAuth(authToken);

        // clear the row in the AUTH table and return success
        return authDao.deleteAuth(authToken);
    }
}
