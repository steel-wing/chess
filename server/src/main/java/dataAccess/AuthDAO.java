package dataAccess;

import model.AuthData;
import model.UserData;

/** Interface for handling getting AuthData from a database */
public interface AuthDAO {
    /** Gets the AuthData that corresponds with a given token (null if there is none) */
    AuthData getAuth(String authToken) throws DataAccessException;

    /** Creates a unique UUID for the user, stores it, and returns the row in AUTH */
    AuthData createAuth(UserData user) throws DataAccessException;

    /** Deletes an AuthToken/User pair, based on the AuthToken */
    void deleteAuth(String authToken) throws DataAccessException;

    /** Clears the entire AUTH database */
    void clear() throws DataAccessException;
}
