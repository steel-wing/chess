package service;

import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.CreateRequest;
import server.Server;

public class CreateService {
    /**
     * @param gameRequest Contains the gameName and authToken
     * @return the new gameID of the created game
     * @throws DataAccessException in the event of authentication or retrieval errors
     */
    public static int create(CreateRequest gameRequest) throws DataAccessException {
        // get values from inputs
        String gameName = gameRequest.gameName();
        String authToken = gameRequest.authToken();

        // get auth data (and throw errors)
        AuthData auth = Server.authDAO.getAuth(authToken);

        // create a new game and return it
        GameData gameData = Server.gameDAO.createGame(gameName);
        return gameData.gameID();
    }
}
