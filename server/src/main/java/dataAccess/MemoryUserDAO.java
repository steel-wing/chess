package dataAccess;
import model.UserData;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private static Map<String, UserData> USER;

    public UserData getUser(String username) throws DataAccessException {
        UserData user = USER.get(username);
        if (user == null) {
            throw new DataAccessException("No such User");
        }
        return user;
    }

    public UserData createUser(String username, UserData data) throws DataAccessException{
        if (USER.get(username) == null) {
            USER.put(username, data);
            return data;
        }
        throw new DataAccessException("User already exists");
    }

    public boolean clear() {
        USER.clear();
        return true;
    }
}

