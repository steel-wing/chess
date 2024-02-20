package dataAccess;

import model.AuthData;

public class AuthDAO {
    public static AuthData SELECT(String username) {
        return MemoryAuthDAO.SELECT(username);
    }

    public static void INSERT(String authToken, String username) {
        AuthData data = new AuthData(authToken, username);
        MemoryAuthDAO.INSERT(authToken, data);
    }
}
