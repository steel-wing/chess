package service;

import server.Server;

public class ClearService {
    /**
     * Wipes all three databases.
     * @return A success boolean.
     */
    public static boolean clear() {
        return Server.authDAO.clear() && Server.gameDAO.clear() && Server.userDAO.clear();
    }
}
