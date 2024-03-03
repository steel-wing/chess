package service;

import dataAccess.DataAccessException;
import dataAccess.ErrorException;
import model.GameData;
import request.JoinRequest;
import server.Server;

public class JoinService {
    /**
     * Handles joining an existing game
     * @param req The join() request, containing teamColor and gameID
     * @return A boolean indicating completion
     * @throws DataAccessException If things go sour
     */
    public static boolean join(JoinRequest req) throws DataAccessException, ErrorException {
        // get values from inputs
        String playerColor = req.playerColor();
        int gameID = req.gameID();
        String username = Server.authDAO.getAuth(req.authToken()).username();

        // get old game contents
        GameData oldData = Server.gameDAO.getGame(req.gameID());

        // check the inputs
        if (gameID == 0 || playerColor != null && !playerColor.isEmpty() &&
            !playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            throw new ErrorException("null value");
        }

        String white = oldData.whiteUsername();
        String black = oldData.blackUsername();

        // verify that the playerColor is good to go
        if (playerColor != null) {
            // handle the white case
            if (playerColor.equals("WHITE")) {
                if (white == null) {
                    white = username;
                } else {
                    throw new DataAccessException("team already taken");
                }
            }

            // handle the black case
            if (playerColor.equals("BLACK")) {
                if (black == null) {
                    black = username;
                } else {
                    throw new DataAccessException("team already taken");
                }
            }
        }

        // update the GameData object in the GAME database
        GameData update = new GameData(gameID, white, black, oldData.gameName(), oldData.game());
        return Server.gameDAO.updateGame(gameID, update);
    }
}
