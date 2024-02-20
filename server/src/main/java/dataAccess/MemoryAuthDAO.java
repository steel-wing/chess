package dataAccess;

import model.AuthData;

import java.util.Map;

public class MemoryAuthDAO {
    private static Map<String, AuthData> auths;

    public static AuthData SELECT(String authToken) {
        return auths.get(authToken);
    }

    public static AuthData INSERT(String authToken, AuthData data) {
        if (auths.get(authToken) == null) {
            auths.put(authToken, data);
            return data;
        }
        return null;
    }

    public static Map<String, AuthData> DELETE() {
        auths.clear();
        return null;
    }

}
