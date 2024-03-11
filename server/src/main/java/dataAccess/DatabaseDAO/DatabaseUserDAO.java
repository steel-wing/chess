package dataAccess.DatabaseDAO;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.UserDAO;
import model.UserData;

import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO {

    public UserData getUser(String username) throws DataAccessException {
        UserData userData = null;

        // select the
        String selectQuery = "SELECT * FROM USER WHERE username = ?";

        try (var conn = DatabaseManager.getConnection();
            // get the table of usernames -> passwords
            var preparedStatement = conn.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, username);

            // run through all results and see if one of them is the User
            try (var table = preparedStatement.executeQuery()) {
                if (table.next()) {
                    String userDataJSON = table.getString("userData");
                    userData = new Gson().fromJson(userDataJSON, UserData.class);
                }
            }

        // handle errors
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(String.format("Unable to get User from username: %s", exception.getMessage()));
        }
        // we couldn't find him
        if (userData == null) {
            throw new DataAccessException("no such User");
        }

        return userData;
    }

    public UserData createUser(String username, UserData data) throws DataAccessException {
        String insertQuery = "INSERT INTO USER (username, userData) VALUES (?, ?)";

        // go create a new line in the USER database
        try (var conn = DatabaseManager.getConnection();
            var preparedStatement = conn.prepareStatement(insertQuery)) {

            Gson gson = new Gson();
            String userData = gson.toJson(data);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, userData);

            preparedStatement.executeUpdate();

        // handle errors
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(String.format("Unable to create User: %s", exception.getMessage()));
        }
        return data;
    }

    public void clear() throws DataAccessException {
        String clearRequest = "DELETE FROM USER";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(clearRequest)) {
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(String.format("Unable to clear USER table: %s", exception.getMessage()));
        }
    }
}
