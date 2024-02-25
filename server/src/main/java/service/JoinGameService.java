package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import request.JoinRequest;

public class JoinGameService {
    /**
     * Handles joining an existing game
     * @param join The join() request, containing teamColor and gameID
     * @param authToken The authToken that was carried in the header
     * @return A boolean indicating completion
     * @throws DataAccessException If things go sour
     */
    public static boolean join(JoinRequest join, String authToken) throws DataAccessException {
        AuthDAO authDao = new MemoryAuthDAO();
        UserDAO userDao = new MemoryUserDAO();
        GameDAO gameDao = new MemoryGameDAO();

        // get values from inputs
        AuthData auth = authDao.getAuth(authToken);
        int gameID = join.gameID();
        String playerColor = join.playerColor();
        String username = auth.username();

        // get old game contents
        GameData oldData = gameDao.getGame(gameID);
        String oldBlack = oldData.blackUsername();
        String oldWhite = oldData.whiteUsername();

        // initialize changing variables
        String newWhiteTeam = "";
        String newBlackTeam = "";

        // handle the observer case (?)
        if (playerColor.isEmpty()) {
            newWhiteTeam = oldWhite;
            newBlackTeam = oldBlack;
        }

        // handle the white case
        if (playerColor.equals("WHITE")) {
            if (oldWhite.isEmpty()) {
                newWhiteTeam = username;
            } else {
                throw new DataAccessException("already taken");
            }
        }

        // handle the black case
        if (playerColor.equals("BLACK")) {
            if (oldBlack.isEmpty()) {
                newBlackTeam = username;
            } else {
                throw new DataAccessException("already taken");
            }
        }

        // update the GameData object in the GAME database
        GameData update = new GameData(gameID, newWhiteTeam, newBlackTeam, oldData.gameName(), oldData.game());
        gameDao.updateGame(gameID, update);

        return true;
    }
}
