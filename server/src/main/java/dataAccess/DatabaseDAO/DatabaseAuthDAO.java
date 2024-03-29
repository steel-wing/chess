package dataAccess.DatabaseDAO;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseAuthDAO implements AuthDAO {
    public AuthData getAuth(String authToken) throws DataAccessException {
        String found = "";
        String getQuery = "SELECT username FROM AUTH WHERE authToken = ?";

        // establish connection
        try (var connect = DatabaseManager.getConnection()) {
            // get the select statement ready
            try (var query = connect.prepareStatement(getQuery)) {
                query.setString(1, authToken);
                // make comparisons: update "username" if you can
                try (ResultSet results = query.executeQuery()) {
                    if (results.next()) {
                        found = results.getString("username");
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to get username from authToken: %s", ex.getMessage()));
        }

        // throw an exception if we found no one
        if (found.isEmpty()) {
            throw new DataAccessException("no such AuthToken");
        }

        return new AuthData(authToken, found);
    }

    public AuthData createAuth(UserData user) throws DataAccessException {
        String username = user.username();
        String authToken = UUID.randomUUID().toString();

        String insertStatement = "INSERT INTO AUTH (authtoken, username) VALUES (?, ?)";

        try (var connect = DatabaseManager.getConnection()) {
            try (var statement = connect.prepareStatement(insertStatement)) {
                statement.setString(1, authToken);
                statement.setString(2, username);
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(String.format("Unable to create authToken: %s", exception.getMessage()));
        }
        return new AuthData(authToken, username);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String deleteQuery = "DELETE FROM AUTH WHERE authToken = ?";

        try (var connect = DatabaseManager.getConnection();
             var statement = connect.prepareStatement(deleteQuery)) {
            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to delete row by auth token: %s", ex.getMessage()));
        }
    }

    public void clear() throws DataAccessException {
        String clearRequest = "DELETE FROM AUTH";

        try (var connect = DatabaseManager.getConnection();
             var statement = connect.prepareStatement(clearRequest)) {
            statement.executeUpdate();
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(String.format("Unable to clear AUTH table: %s", exception.getMessage()));
        }
    }
}
