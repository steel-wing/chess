package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import request.CreateRequest;

public class CreateGameService {
    /**
     * @param gameRequest Contains the gameName and authToken
     * @return the new gameID of the created game
     * @throws DataAccessException in the event of authentication or retrieval errors
     */
    public static int create(CreateRequest gameRequest) throws DataAccessException {
        // initialize our DAOs
        AuthDAO authDao = new MemoryAuthDAO();
        GameDAO gameDao = new MemoryGameDAO();

        // get values from inputs
        String gameName = gameRequest.gameName();
        String authToken = gameRequest.authToken();

        // get auth data (and throw errors)
        AuthData auth = authDao.getAuth(authToken);

        // create a new game and return it
        GameData gameData = gameDao.createGame(gameName);
        return gameData.gameID();
    }
}
