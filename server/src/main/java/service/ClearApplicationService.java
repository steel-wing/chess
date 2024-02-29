package service;

import server.Server;

public class ClearApplicationService {
    public static boolean clear() {
        return Server.authDAO.clear() && Server.gameDAO.clear() && Server.gameDAO.clear();
    }
}
