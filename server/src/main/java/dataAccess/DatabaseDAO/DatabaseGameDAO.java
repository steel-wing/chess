package dataAccess.DatabaseDAO;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.GameDAO;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class DatabaseGameDAO implements GameDAO {
    public GameData createGame(String gameName) throws DataAccessException {
        // cronch the UUID down into an int
        int gameID = Math.abs(UUID.randomUUID().hashCode());

        // build a new game
        ChessGame chessGame = new ChessGame();

        // put the game and its ID into the GAME database
        GameData data = new GameData(gameID, null, null, gameName, chessGame);

        String game = new Gson().toJson(data);
        String insertQuery = "INSERT INTO GAME (gameID, gameData) VALUES (?, ?)";

        try (var connect = DatabaseManager.getConnection();
            var query = connect.prepareStatement(insertQuery)) {
                query.setInt(1, gameID);
                query.setString(2, game);
                query.executeUpdate();
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(String.format("Unable to create game: %s", exception.getMessage()));
        }

        return data;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = null;

        String getQuery = "SELECT gameData FROM GAME WHERE gameID = ?";

        // establish connection
        try (var connect = DatabaseManager.getConnection();
             var query = connect.prepareStatement(getQuery)) {
            query.setInt(1, gameID);
            // go find the game
            try (var resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    String gameData = resultSet.getString("gameData");
                    game = new Gson().fromJson(gameData, GameData.class);
                }
            }
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(String.format("Unable to retrieve gameData: %s", exception.getMessage()));
        }

        return game;
    }

    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();

        String selectQuery = "SELECT gameData FROM GAME";

        try (var connect = DatabaseManager.getConnection();
             var query = connect.prepareStatement(selectQuery);
             ResultSet results = query.executeQuery()) {
            // while loop to handle putting games into the list
            while (results.next()) {
                String json = results.getString("gameData");
                GameData big = new Gson().fromJson(json, GameData.class);

                // forbidden technique to remove the actual board data from the returned list, to only send necessary data
                ChessGame small = new ChessGame();
                small.setWinner(big.game().getWinner());
                small.setBoard(null);
                GameData game = new GameData(big.gameID(), big.whiteUsername(), big.blackUsername(), big.gameName(), small);
                games.add(game);
            }
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(String.format("Unable to retrieve GAME: %s", exception.getMessage()));
        }

        return games;
    }

    public boolean updateGame(int gameID, GameData update) throws DataAccessException {
        String updateQuery = "UPDATE GAME SET gameData = ? WHERE gameID = ?";

        try (var connect = DatabaseManager.getConnection();
             var query = connect.prepareStatement(updateQuery)) {

            String gameData = new Gson().toJson(update);

            query.setString(1, gameData);
            query.setInt(2, gameID);
            query.executeUpdate();

        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to update gameData: %s", ex.getMessage()));
        }

        return true;
    }

    public void clear() throws DataAccessException {
        String deleteStatement = "DELETE FROM GAME";

        try (var connect = DatabaseManager.getConnection();
             var statement = connect.prepareStatement(deleteStatement)) {
            statement.executeUpdate();
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to delete all rows from the gameIdToGame table: %s", ex.getMessage()));
        }
    }
}
