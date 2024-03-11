package service;

import dataAccess.DataAccessException;
import dataAccess.ErrorException;
import model.AuthData;
import server.Server;

public class LogoutService {
    /** Handles logging out
     *
     * @param authToken the incoming authToken
     * @throws DataAccessException If things go sour
     * @throws ErrorException also
     */
    public static void logout(String authToken) throws DataAccessException, ErrorException {
        // bad request if no authToken provided
        if (authToken == null) {
            throw new ErrorException("bad request");
        }

        // get auth data (and throw errors)
        AuthData authData = Server.authDAO.getAuth(authToken);

        Server.authDAO.deleteAuth(authToken);
    }
}
