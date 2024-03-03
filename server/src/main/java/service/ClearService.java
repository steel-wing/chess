package service;

import server.Server;

public class ClearService {
    /**
     * Wipes all three databases.
     */
    public static void clear() {
        Server.authDAO.clear();
        Server.gameDAO.clear();
        Server.userDAO.clear();
    }
}
