package dataAccess.MemoryDAO;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private static final Map<String, UserData> USER = new HashMap<>();

    public UserData getUser(String username) throws DataAccessException {
        UserData found = USER.get(username);
        if (found == null) {
            throw new DataAccessException("No such User");
        }
        return found;
    }

    public UserData createUser(String username, UserData data) throws DataAccessException{
        if (USER.get(username) == null) {
            USER.put(username, data);
            return data;
        }
        throw new DataAccessException("User already exists");
    }

    public void clear() {
        USER.clear();
    }
}

