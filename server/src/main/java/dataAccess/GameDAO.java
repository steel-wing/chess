package dataAccess;
import model.GameData;
import java.util.ArrayList;

/** Interface for handling getting GameData from a database */
public interface GameDAO {

    /** Creates a new game based on the data provided */
    GameData createGame(String gameName, String authToken);

    /** Gets the gamedata corresponding to the gameID */
    GameData getGame(String gameID);

    /** Lists all games in the GAME database */
    ArrayList<GameData> listGames();

    /** Updates the gamedata corresponding to the gameID */
    boolean updateGame(String gameID);

    /** Clears the entire GAME database */
    boolean clear();
}
