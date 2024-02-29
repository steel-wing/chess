package service;

import server.Server;

public class ClearService {
    public static boolean clear() {
        return Server.authDAO.clear() && Server.gameDAO.clear() && Server.userDAO.clear();
    }
}
