package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import request.JoinRequest;

public class JoinGameService {
    /**
     * Handles joining an existing game
     * @param joinRequest The join() request, containing teamColor and gameID
     * @return A boolean indicating completion
     * @throws DataAccessException If things go sour
     */
    public static boolean join(JoinRequest joinRequest) throws DataAccessException {
        AuthDAO authDao = new MemoryAuthDAO();
        UserDAO userDao = new MemoryUserDAO();
        GameDAO gameDao = new MemoryGameDAO();

        // get values from inputs
        String playerColor = joinRequest.playerColor();
        int gameID = joinRequest.gameID();
        AuthData auth = authDao.getAuth(joinRequest.authToken());
        String username = auth.username();

        // get old game contents
        GameData oldData = gameDao.getGame(gameID);
        String oldBlack = oldData.blackUsername();
        String oldWhite = oldData.whiteUsername();

        // initialize changing variables
        String newWhiteTeam = null;
        String newBlackTeam = null;

        // handle the observer case
        if (playerColor == null) {
            newWhiteTeam = oldWhite;
            newBlackTeam = oldBlack;
        } else {
            // handle the white case
            if (playerColor.equals("WHITE")) {
                if (oldWhite == null) {
                    newWhiteTeam = username;
                } else {
                    throw new DataAccessException("already taken");
                }
            }

            // handle the black case
            if (playerColor.equals("BLACK")) {
                if (oldBlack == null) {
                    newBlackTeam = username;
                } else {
                    throw new DataAccessException("already taken");
                }
            }
        }

        // update the GameData object in the GAME database
        GameData update = new GameData(gameID, newWhiteTeam, newBlackTeam, oldData.gameName(), oldData.game());
        return gameDao.updateGame(gameID, update);
    }
}
