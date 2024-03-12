package dataAccess.DatabaseDAO;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.UserDAO;
import model.UserData;

import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO {

    public UserData getUser(String username) throws DataAccessException {
        UserData user = null;

        // select the
        String selectQuery = "SELECT * FROM USER WHERE username = ?";

        try (var connect = DatabaseManager.getConnection();
            var query = connect.prepareStatement(selectQuery)) {
            // get the table of usernames -> passwords
            query.setString(1, username);

            // run through all results and see if one of them is the User
            try (var table = query.executeQuery()) {
                if (table.next()) {
                    String userData = table.getString("userData");
                    user = new Gson().fromJson(userData, UserData.class);
                }
            }

        // handle errors
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(String.format("Unable to get User from username: %s", exception.getMessage()));
        }
        // we couldn't find him
        if (user == null) {
            throw new DataAccessException("no such User");
        }

        return user;
    }

    public UserData createUser(String username, UserData data) throws DataAccessException {
        String insertQuery = "INSERT INTO USER (username, userData) VALUES (?, ?)";

        // go create a new line in the USER database
        try (var connect = DatabaseManager.getConnection();
            var query = connect.prepareStatement(insertQuery)) {

            String userData = new Gson().toJson(data);

            query.setString(1, username);
            query.setString(2, userData);

            query.executeUpdate();

        // handle errors
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(String.format("Unable to create User: %s", exception.getMessage()));
        }
        return data;
    }

    public void clear() throws DataAccessException {
        String clearRequest = "DELETE FROM USER";

        try (var connect = DatabaseManager.getConnection();
             var statement = connect.prepareStatement(clearRequest)) {
            statement.executeUpdate();
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(String.format("Unable to clear USER table: %s", exception.getMessage()));
        }
    }
}
