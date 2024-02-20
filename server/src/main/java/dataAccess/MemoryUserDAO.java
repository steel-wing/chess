package dataAccess;

import model.UserData;

import java.util.Map;

public class MemoryUserDAO {
    private static Map<String, UserData> users;

    public static UserData SELECT(String username) {
        return users.get(username);
    }

    public static UserData INSERT(String username, UserData data) {
        if (users.get(username) == null) {
            users.put(username, data);
            return data;
        }
        return null;
    }

    public static Map<String, UserData> DELETE() {
        users.clear();
        return null;
    }
}
