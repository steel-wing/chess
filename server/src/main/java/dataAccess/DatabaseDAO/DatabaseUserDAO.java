package dataAccess.DatabaseDAO;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;

public class DatabaseUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public UserData createUser(String username, UserData data) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {

    }
}
