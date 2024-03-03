package dataAccess;

import model.GameData;

import java.util.ArrayList;

/** Interface for handling getting GameData from a database */
public interface GameDAO {
    /** Creates a new game based on the data provided */
    GameData createGame(String gameName) throws DataAccessException ;

    /** Gets the gamedata corresponding to the gameID */
    GameData getGame(int gameID);

    /** Lists all games in the GAME database, numbering on one side, gameID's on the other */
    ArrayList<GameData> listGames();

    /** Updates the gamedata corresponding to the gameID */
    boolean updateGame(int gameID, GameData update);

    /** Clears the entire GAME database */
    void clear();
}
