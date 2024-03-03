package service;

import dataAccess.DataAccessException;
import dataAccess.ErrorException;
import model.AuthData;
import model.GameData;
import request.CreateRequest;
import server.Server;

public class CreateService {
    /**
     * @param req Contains the gameName and authToken
     * @return the new gameID of the created game
     * @throws DataAccessException in the event of authentication or retrieval errors
     * @throws ErrorException also
     */
    public static int create(CreateRequest req) throws DataAccessException, ErrorException {
        // get values from inputs
        String gameName = req.gameName();
        String authToken = req.authToken();

        // check the input
        if (gameName == null || authToken == null) {
            throw new ErrorException("null value");
        }

        // get auth data (and throw errors)
        AuthData auth = Server.authDAO.getAuth(authToken);

        // create a new game and return it
        GameData gameData = Server.gameDAO.createGame(gameName);
        return gameData.gameID();
    }
}
