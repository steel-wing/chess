package service;

import dataAccess.DataAccessException;
import server.Server;

public class ClearService {
    /**
     * Wipes all three databases.
     */
    public static void clear() throws DataAccessException {
        Server.authDAO.clear();
        Server.gameDAO.clear();
        Server.userDAO.clear();
    }
}
