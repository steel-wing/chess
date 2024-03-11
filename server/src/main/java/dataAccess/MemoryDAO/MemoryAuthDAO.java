package dataAccess.MemoryDAO;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private static final Map<String, AuthData> AUTH = new HashMap<>();

    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData found = AUTH.get(authToken);
        if (found == null) {
            throw new DataAccessException("no such AuthToken");
        }
        return found;
    }

    public AuthData createAuth(UserData user) {
        String authToken = UUID.randomUUID().toString();
        AuthData data = new AuthData(authToken, user.username());
        AUTH.put(authToken, data);
        return data;
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        if (AUTH.get(authToken) == null) {
            throw new DataAccessException("no such AuthToken");
        }
        AUTH.remove(authToken);
    }

    public void clear() {
        AUTH.clear();
    }
}
