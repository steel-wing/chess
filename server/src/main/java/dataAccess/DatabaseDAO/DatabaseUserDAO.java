package dataAccess.DatabaseDAO;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;

public class DatabaseUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) throws DataAccessException {
        // if there is no database, return null

        // if there is no table, return null

        // if there is a table, retrieve the user data from it


        UserData user = new UserData(username, password, email);

        return user;
    }

    @Override
    public UserData createUser(String username, UserData data) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {

    }
}
