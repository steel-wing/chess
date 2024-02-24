package dataAccess;

import model.UserData;

/** Interface for handling getting UserData from a database */
public interface UserDAO {
    /** Gets the userdata corresponding to a username*/
    UserData getUser(String username) throws DataAccessException;

    /** Creates a new user, returns null if they already exist */
    UserData createUser(String username, UserData data) throws DataAccessException;

    /** Clears the entire USER database */
    boolean clear();
}
