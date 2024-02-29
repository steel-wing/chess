package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import server.Server;

import java.util.ArrayList;

public class ListService {
    /**
     *
     * @param authToken An authToken. That's it.
     * @return An ArrayList of all of the GameData currently in the database
     * @throws DataAccessException In event of authToken issues
     */
    public static ArrayList<GameData> list(String authToken) throws DataAccessException {
        // get auth data (and throw errors)
        AuthData authData = Server.authDAO.getAuth(authToken);

        // return the list from the GAME database
        return Server.gameDAO.listGames();
    }
}
