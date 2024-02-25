package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import request.ListRequest;

import java.util.ArrayList;

public class ListGamesService {
    public static ArrayList<GameData> list(ListRequest list) throws DataAccessException {
        // initialize our DAOs
        AuthDAO authDao = new MemoryAuthDAO();
        GameDAO gameDao = new MemoryGameDAO();

        // get session token and corresponding auth data
        String authToken = list.authToken();
        AuthData authData = authDao.getAuth(authToken);

        // return false if the token is not in the AUTH table
        if (authData == null) {
            return null;
        }

        // return the list from the GAME database
        return gameDao.listGames();
    }
}
