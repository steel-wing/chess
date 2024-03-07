package dataAccess.DatabaseDAO;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;

import java.util.ArrayList;

public class DatabaseGameDAO implements GameDAO {
    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public boolean updateGame(int gameID, GameData update) {
        return false;
    }

    @Override
    public void clear() {

    }
}
