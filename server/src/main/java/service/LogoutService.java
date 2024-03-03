package service;

import dataAccess.*;
import model.*;
import server.Server;

public class LogoutService {
    /** Handles logging out
     *
     * @param authToken the incoming authToken
     * @throws DataAccessException If things go sour
     * @throws ErrorException also
     */
    public static void logout(String authToken) throws DataAccessException, ErrorException {
        // get auth data (and throw errors)
        AuthData authData = Server.authDAO.getAuth(authToken);

        // bad request if no authToken provided
        if (authToken == null) {
            throw new ErrorException("bad request");
        }

        Server.authDAO.deleteAuth(authToken);
    }
}
