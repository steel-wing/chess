package dataAccess.MemoryDAO;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryGameDAO implements GameDAO {
    private static final Map<Integer, GameData> GAME = new HashMap<>();

    public GameData createGame(String gameName) throws DataAccessException {
        // cronch the UUID down into an int
        int gameID = Math.abs(UUID.randomUUID().hashCode());

        // build a new game
        ChessGame game = new ChessGame();

        // put the game and its ID into the GAME database
        GameData data = new GameData(gameID, null, null, gameName, game);
        GAME.put(gameID, data);

        return data;
    }

    public GameData getGame(int gameID) {
        return GAME.get(gameID);
    }


    public ArrayList<GameData> listGames() {
        // forbidden type conversions to allow for sorting
        return new ArrayList<>(GAME.values());
    }

    public boolean updateGame(int gameID, GameData update) {
        // verify that gameID exists
        if (!GAME.containsKey(gameID)){
            return false;
        }
        GAME.put(gameID, update);
        return true;
    }

    public void clear() {
        GAME.clear();
    }

}
