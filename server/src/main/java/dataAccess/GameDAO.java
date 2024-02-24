package dataAccess;

import model.GameData;

import java.util.Map;

/** Interface for handling getting GameData from a database */
public interface GameDAO {

    /** Creates a new game based on the data provided */
    GameData createGame(String gameName);

    /** Gets the gamedata corresponding to the gameID */
    GameData getGame(int gameID);

    /** Lists all games in the GAME database, numbering on one side, gameID's on the other */
    Map<Integer, Integer> listGames();

    /** Updates the gamedata corresponding to the gameID */
    boolean updateGame(int gameID, GameData update);

    /** Clears the entire GAME database */
    boolean clear();
}
