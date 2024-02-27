package service;

import dataAccess.*;

public class ClearApplicationService {
    public static boolean clear() {
        AuthDAO authDao = new MemoryAuthDAO();
        UserDAO userDao = new MemoryUserDAO();
        GameDAO gameDao = new MemoryGameDAO();

        return authDao.clear() && userDao.clear() && gameDao.clear();
    }
}
