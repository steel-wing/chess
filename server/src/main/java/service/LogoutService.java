package service;

import dataAccess.*;
import model.*;
import server.Server;

public class LogoutService {
    /** Handles logging out
     *
     * @param authToken the incoming authToken
     * @return A success boolean
     * @throws DataAccessException If things go sour
     */
    public static boolean logout(String authToken) throws DataAccessException {
        // get auth data (and throw errors)
        AuthData authData = Server.authDAO.getAuth(authToken);

        // clear the row in the AUTH table and return success
        return Server.authDAO.deleteAuth(authToken);
    }
}
